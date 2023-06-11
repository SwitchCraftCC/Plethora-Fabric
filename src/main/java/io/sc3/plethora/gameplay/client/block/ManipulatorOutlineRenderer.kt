package io.sc3.plethora.gameplay.client.block

import io.sc3.plethora.gameplay.manipulator.ManipulatorBlock
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.WorldRenderer
import net.minecraft.util.hit.HitResult

// private const val GLOW_OFFSET = 0.005f
// private const val GLOW_PERIOD = 20.0

object ManipulatorOutlineRenderer {
  // private val layer = RenderLayer.getEntityTranslucent(ModId("textures/misc/white.png"))
  private var ticks = 0f

  @JvmStatic
  fun onBlockOutline(worldCtx: WorldRenderContext, ctx: BlockOutlineContext): Boolean {
    ticks += worldCtx.tickDelta()

    val world = ctx.entity().world
    val pos = ctx.blockPos()

    val state = world.getBlockState(pos)
    val manipulator = state.block as? ManipulatorBlock ?: return true

    val result = MinecraftClient.getInstance().crosshairTarget
    if (result == null || result.type != HitResult.Type.BLOCK) return true

    val facing = state.get(ManipulatorBlock.FACING)

    val hit = result.pos.subtract(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
    val type = manipulator.type

    val facingBoxes = type.boxesFor(facing.opposite)
    // val upBoxes     = type.boxesFor(Direction.UP)

    val hitBoxId = facingBoxes
      .map { it.expand(ManipulatorBlock.BOX_EXPAND) }
      .indexOfFirst { it.contains(hit) }
      .takeIf { it >= 0 } ?: return true

    val cameraPos = worldCtx.camera().pos.negate()
    val hitBox    = facingBoxes[hitBoxId].offset(pos).offset(cameraPos)
    // val upBox     = upBoxes[hitBoxId]

    val matrixStack = worldCtx.matrixStack()

    // Glowing square (use the box from the up rotation and then apply the rotation to the matrix)
    /*matrixStack.push()
    matrixStack.translate(cameraPos.x, cameraPos.y, cameraPos.z)
    matrixStack.translate(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

    matrixStack.translate(0.5, 0.5, 0.5)
    matrixStack.multiply(facing.opposite.rotationQuaternion)
    matrixStack.translate(-0.5, -0.5, -0.5)

    val glow = worldCtx.consumers()!!.getBuffer(layer)
    val matrix4f = matrixStack.peek().positionMatrix

    val alpha = 0.4f + sin(ticks / GLOW_PERIOD).toFloat() * 0.1f
    vertex(glow, matrix4f, upBox.minX, upBox.minY + GLOW_OFFSET, upBox.minZ, 0f, 1f, alpha)
    vertex(glow, matrix4f, upBox.maxX, upBox.minY + GLOW_OFFSET, upBox.minZ, 1f, 1f, alpha)
    vertex(glow, matrix4f, upBox.maxX, upBox.minY + GLOW_OFFSET, upBox.maxZ, 1f, 0f, alpha)
    vertex(glow, matrix4f, upBox.minX, upBox.minY + GLOW_OFFSET, upBox.maxZ, 0f, 0f, alpha)

    matrixStack.pop()*/

    // Box outline
    val outline = worldCtx.consumers()!!.getBuffer(RenderLayer.getLines())
    WorldRenderer.drawBox(matrixStack, outline, hitBox, 1.0f, 1.0f, 1.0f, 0.4f)

    return false
  }

  /*private fun vertex(consumer: VertexConsumer, matrix4f: Matrix4f, x: Double, y: Double, z: Double,
                     u: Float, v: Float, alpha: Float) {
    consumer // POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
      .vertex(matrix4f, x.toFloat(), y.toFloat(), z.toFloat())
      .color(1f, 1f, 1f, alpha)
      .texture(u, v)
      .overlay(OverlayTexture.DEFAULT_UV)
      .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
      .normal(0.0f, 0.0f, 1.0f)
      .next()
  }*/
}
