package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.LINE_LOOP_2D
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable
import pw.switchcraft.plethora.util.DirtyingProperty
import javax.annotation.Nonnull

class LineLoop2d(
  id: Int,
  parent: Int
) : Polygon2d(id, parent, LINE_LOOP_2D), Scalable {
  /** Line thickness */
  override var scale by DirtyingProperty(1f)

  override fun writeInitial(@Nonnull buf: PacketByteBuf) {
    super.writeInitial(buf)
    buf.writeFloat(scale)
  }

  override fun readInitial(@Nonnull buf: PacketByteBuf) {
    super.readInitial(buf)
    scale = buf.readFloat()
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    if (points.size < 2) return

    setupFlat()
    RenderSystem.lineWidth(scale)

    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix
    val normal = matrices.peek().normalMatrix

    RenderSystem.setShader { GameRenderer.getRenderTypeLinesShader() }
    buffer.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.LINES)

    for (point in points) {
      buffer
        .vertex(matrix, point.x.toFloat(), point.y.toFloat(), 0f)
        .color(red, green, blue, alpha)
        .normal(normal, 0f, 1f, 0f).next()
    }

    // No OpenGL LINE_LOOP anymore, so close the loop manually
    val first = points[0]
    buffer
      .vertex(matrix, first.x.toFloat(), first.y.toFloat(), 0f)
      .color(red, green, blue, alpha)
      .normal(normal, 0f, 1f, 0f).next()

    BufferRenderer.drawWithShader(buffer.end())
    RenderSystem.lineWidth(1f)
  }
}
