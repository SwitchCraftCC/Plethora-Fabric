package io.sc3.plethora.gameplay.modules.glasses.objects.object3d

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.SimpleFramebuffer
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler.HEIGHT
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler.WIDTH
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectGroup
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.FRAME_3D
import io.sc3.plethora.mixin.client.GameRendererAccessor
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.DirtyingProperty

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
    val gr = mc.gameRenderer
    val cam = gr.camera
    val w = WIDTH.toFloat(); val h = HEIGHT.toFloat()

    val currentBuffer = GlStateManager.getBoundFramebuffer()
    val currentFog = RenderSystem.getShaderFogEnd()
    RenderSystem.setShaderFogEnd(2000.0f)

    // ==============================

    RenderSystem.backupProjectionMatrix()

    val matrixStack = MatrixStack()
    matrixStack.peek().positionMatrix.identity()

    val fov = (gr as GameRendererAccessor).invokeGetFov(cam, mc.tickDelta, true)

    matrixStack.peek()
      .positionMatrix
      .mul(Matrix4f().setPerspective((fov * (Math.PI / 180.0).toFloat()).toFloat(), w / h, 0.1f, 1000.0f))

    val projection = matrixStack.peek().positionMatrix
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

    RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
    RenderSystem.setShaderTexture(0, framebuffer.colorAttachment)
    RenderSystem.enableBlend()

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
    buffer.vertex(matrix, 0.0f, h, 0.0f).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    buffer.vertex(matrix, w, h, 0.0f).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    buffer.vertex(matrix, w, 0.0f, 0.0f).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    buffer.vertex(matrix, 0.0f, 0.0f, 0.0f).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 0.2f).next()
    BufferRenderer.drawWithGlobalProgram(buffer.end())

    RenderSystem.setShaderFogEnd(currentFog)

    matrices.pop()
  }

  companion object {
    val framebuffer by lazy {
      Plethora.log.debug("Creating ObjectFrame3d framebuffer with size $WIDTH x $HEIGHT")
      SimpleFramebuffer(WIDTH, HEIGHT, true, true)
    }
  }
}