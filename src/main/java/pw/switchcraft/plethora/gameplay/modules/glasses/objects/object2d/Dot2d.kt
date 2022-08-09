package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.DOT_2D
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable
import pw.switchcraft.plethora.util.ByteBufUtils
import pw.switchcraft.plethora.util.DirtyingProperty
import pw.switchcraft.plethora.util.Vec2d

class Dot2d(
  id: Int,
  parent: Int
) : ColourableObject(id, parent, DOT_2D), Positionable2d, Scalable {
  override var position by DirtyingProperty(Vec2d.ZERO!!)
  override var scale by DirtyingProperty(1f)

  override fun writeInitial(buf: PacketByteBuf) {
    super.writeInitial(buf)
    ByteBufUtils.writeVec2d(buf, position)
    buf.writeFloat(scale)
  }

  override fun readInitial(buf: PacketByteBuf) {
    super.readInitial(buf)
    position = ByteBufUtils.readVec2d(buf)
    scale = buf.readFloat()
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    setupFlat()

    val x = position.x().toFloat(); val y = position.y().toFloat()
    val delta = scale / 2

    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix

    buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)

    buffer.vertex(matrix, x - delta, y - delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x - delta, y + delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x + delta, y + delta, 0f).color(red, green, blue, alpha).next()

    buffer.vertex(matrix, x - delta, y - delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x + delta, y + delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x + delta, y - delta, 0f).color(red, green, blue, alpha).next()

    BufferRenderer.drawWithShader(buffer.end())
  }
}
