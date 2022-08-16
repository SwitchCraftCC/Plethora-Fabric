package pw.switchcraft.plethora.gameplay.client.block

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Matrix4f
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock
import kotlin.math.sin

private const val GLOW_OFFSET = 0.005f
private const val GLOW_PERIOD = 20.0

object ManipulatorOutlineRenderer {
  private val layer = RenderLayer.getEntityTranslucent(Identifier(Plethora.MOD_ID, "textures/misc/white.png"))
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
    val down = facing == Direction.DOWN

    val hit = result.pos.subtract(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
    val type = manipulator.type

    for (box in type.boxesFor(facing)) {
      val expandBox = box.expand(ManipulatorBlock.BOX_EXPAND)
      if (expandBox.contains(hit)) {
        val rb = expandBox.offset(pos).offset(worldCtx.camera().pos.negate())
        val matrixStack = worldCtx.matrixStack()

        // Glowing square
        if (down) {
          // TODO: Because Boxes, this doesn't work for any direction except DOWN. Fix later
          val glow = worldCtx.consumers()!!.getBuffer(layer)
          val matrix4f = matrixStack.peek().positionMatrix

          val alpha = 0.4f + sin(ticks / GLOW_PERIOD).toFloat() * 0.1f
          vertex(glow, matrix4f, rb.minX, rb.minY + GLOW_OFFSET, rb.minZ, 0f, 1f, alpha)
          vertex(glow, matrix4f, rb.maxX, rb.minY + GLOW_OFFSET, rb.minZ, 1f, 1f, alpha)
          vertex(glow, matrix4f, rb.maxX, rb.minY + GLOW_OFFSET, rb.maxZ, 1f, 0f, alpha)
          vertex(glow, matrix4f, rb.minX, rb.minY + GLOW_OFFSET, rb.maxZ, 0f, 0f, alpha)
        }

        // Box outline
        val outline = worldCtx.consumers()!!.getBuffer(RenderLayer.getLines())

        WorldRenderer.drawBox(matrixStack, outline, rb, 0.0f, 0.0f, 0.0f, 0.4f)

        return false
      }
    }
    return true
  }

  private fun vertex(consumer: VertexConsumer, matrix4f: Matrix4f, x: Double, y: Double, z: Double,
                     u: Float, v: Float, alpha: Float) {
    consumer // POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
      .vertex(matrix4f, x.toFloat(), y.toFloat(), z.toFloat())
      .color(1f, 1f, 1f, alpha)
      .texture(u, v)
      .overlay(OverlayTexture.DEFAULT_UV)
      .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
      .normal(0.0f, 0.0f, 1.0f)
      .next()
  }
}
