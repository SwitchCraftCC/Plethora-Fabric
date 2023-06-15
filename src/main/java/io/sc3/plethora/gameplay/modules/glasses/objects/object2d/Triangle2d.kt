package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import com.google.common.base.Objects
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.objects.ColourableObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.TRIANGLE_2D
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.Vec2d
import io.sc3.plethora.util.Vec2d.ZERO
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.network.PacketByteBuf
import java.util.*

class Triangle2d(
  id: Int,
  parent: Int
) : ColourableObject(id, parent, TRIANGLE_2D), MultiPoint2d {
  private val points = arrayOf(ZERO, ZERO, ZERO)

  init {
    Arrays.fill(points, ZERO)
  }

  override fun getPoint(idx: Int) = points[idx]

  override fun setVertex(idx: Int, point: Vec2d) {
    if (!Objects.equal(points[idx], point)) {
      points[idx] = point
      setDirty()
    }
  }

  override val vertices: Int
    get() = 3

  override fun writeInitial(buf: PacketByteBuf) {
    super.writeInitial(buf)
    for (point in points) ByteBufUtils.writeVec2d(buf, point)
  }

  override fun readInitial(buf: PacketByteBuf) {
    super.readInitial(buf)
    for (i in points.indices) points[i] = ByteBufUtils.readVec2d(buf)
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, ctx: DrawContext, consumers: VertexConsumerProvider?) {
    setupFlat()

    val matrices = ctx.matrices
    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix

    buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)
    buffer.vertex(matrix, points[0].x.toFloat(), points[0].y.toFloat(), 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, points[1].x.toFloat(), points[1].y.toFloat(), 0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, points[2].x.toFloat(), points[2].y.toFloat(), 0f).color(red, green, blue, alpha).next()
    BufferRenderer.drawWithGlobalProgram(buffer.end())
  }
}
