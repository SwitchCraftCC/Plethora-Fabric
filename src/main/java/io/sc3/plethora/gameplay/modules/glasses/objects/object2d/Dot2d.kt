package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.objects.ColourableObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.DOT_2D
import io.sc3.plethora.gameplay.modules.glasses.objects.Scalable
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.DirtyingProperty
import io.sc3.plethora.util.Vec2d
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.network.PacketByteBuf

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
  override fun draw(canvas: CanvasClient, ctx: DrawContext, consumers: VertexConsumerProvider?) {
    setupFlat()

    val x = position.x().toFloat(); val y = position.y().toFloat()
    val delta = scale / 2

    val matrices = ctx.matrices
    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix

    buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)

    buffer.vertex(matrix, x - delta, y - delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x - delta, y + delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x + delta, y + delta, 0f).color(red, green, blue, alpha).next()

    buffer.vertex(matrix, x - delta, y - delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x + delta, y + delta, 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, x + delta, y - delta, 0f).color(red, green, blue, alpha).next()

    BufferRenderer.drawWithGlobalProgram(buffer.end())
  }
}
