package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.SimpleFramebuffer
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.HEIGHT
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.WIDTH
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.FRAME_3D
import pw.switchcraft.plethora.util.ByteBufUtils
import pw.switchcraft.plethora.util.DirtyingProperty

private const val SCALE = 1 / 64.0f

class ObjectFrame3d(
  id: Int,
  parent: Int
) : BaseObject(id, parent, FRAME_3D), ObjectGroup.Frame2d, Positionable3d, Rotatable3d, DepthTestable {
  override var position by DirtyingProperty(Vec3d.ZERO!!)
  override var rotation: Vec3d? by DirtyingProperty(null)
  override var hasDepthTest by DirtyingProperty(true)

  override fun readInitial(buf: PacketByteBuf) {
    position = ByteBufUtils.readVec3d(buf)
    rotation = ByteBufUtils.readOptVec3d(buf)
    hasDepthTest = buf.readBoolean()
  }

  override fun writeInitial(buf: PacketByteBuf) {
    ByteBufUtils.writeVec3d(buf, position)
    ByteBufUtils.writeOptVec3d(buf, rotation)
    buf.writeBoolean(hasDepthTest)
  }

  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    val children = canvas.getChildren(id) ?: return

    val mc = MinecraftClient.getInstance()
    val w = WIDTH.toFloat(); val h = HEIGHT.toFloat()

    val currentBuffer = GlStateManager.getBoundFramebuffer()
    val currentFog = RenderSystem.getShaderFogEnd()
    RenderSystem.setShaderFogEnd(2000.0f)

    // ==============================

    RenderSystem.backupProjectionMatrix()

    val projection = Matrix4f.projectionMatrix(0.0f, w, h, 0.0f, 0.1f, 1000.0f)
    RenderSystem.setProjectionMatrix(projection)

    val matrixStack2 = MatrixStack()
    matrixStack2.push()
    matrixStack2.loadIdentity()
    matrixStack2.translate(0.0, 0.0, -500.0)

    RenderSystem.colorMask(true, true, true, true)
    framebuffer.setClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC)
    framebuffer.beginWrite(true)

    RenderSystem.disableDepthTest()
    canvas.drawChildren(children.iterator(), matrixStack2, consumers)

    matrixStack2.pop()

    RenderSystem.viewport(0, 0, mc.window.framebufferWidth, mc.window.framebufferHeight)
    RenderSystem.restoreProjectionMatrix()
    framebuffer.endWrite()
    GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, currentBuffer)

    // ===============================

    matrices.push()

    matrices.translate(position.x, position.y, position.z)
    matrices.scale(SCALE, SCALE, SCALE)
    applyRotation(matrices)

    if (hasDepthTest) {
      RenderSystem.enableDepthTest()
    } else {
      RenderSystem.disableDepthTest()
    }

    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix

    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
    RenderSystem.setShaderTexture(0, framebuffer.colorAttachment)
    RenderSystem.enableBlend()

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
    buffer.vertex(matrix, 0.0f, h, 0.0f).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    buffer.vertex(matrix, w, h, 0.0f).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    buffer.vertex(matrix, w, 0.0f, 0.0f).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    BufferRenderer.drawWithShader(buffer.end())

    RenderSystem.setShaderFogEnd(currentFog)

    matrices.pop()
  }

  companion object {
    val framebuffer by lazy {
      Plethora.LOG.info("Creating ObjectFrame3d framebuffer with size $WIDTH x $HEIGHT")
      SimpleFramebuffer(WIDTH, HEIGHT, true, true)
    }
  }
}
