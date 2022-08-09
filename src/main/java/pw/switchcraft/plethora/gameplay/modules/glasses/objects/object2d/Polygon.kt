package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d

import com.google.common.base.Objects
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.PacketByteBuf.getMaxValidator
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.POLYGON_2D
import pw.switchcraft.plethora.util.ByteBufUtils
import pw.switchcraft.plethora.util.Vec2d
import javax.annotation.Nonnull

open class Polygon(
  id: Int,
  parent: Int,
  type: Byte = POLYGON_2D
) : ColourableObject(id, parent, type), MultiPointResizable2d {
  protected var points = mutableListOf<Vec2d>()

  override fun setVertex(idx: Int, point: Vec2d) {
    if (!Objects.equal(points[idx], point)) {
      points[idx] = point
      setDirty()
    }
  }

  override val vertices: Int
    get() = points.size

  override fun getPoint(idx: Int): Vec2d = points[idx]

  override fun addPoint(idx: Int, @Nonnull point: Vec2d) {
    if (idx == points.size) {
      points.add(point)
    } else {
      points.add(idx, point)
    }
    setDirty()
  }

  override fun removePoint(idx: Int) {
    points.removeAt(idx)
    setDirty()
  }

  override fun writeInitial(buf: PacketByteBuf) {
    super.writeInitial(buf)
    buf.writeCollection(points, ByteBufUtils::writeVec2d)
  }

  override fun readInitial(buf: PacketByteBuf) {
    super.readInitial(buf)
    points = buf.readCollection(getMaxValidator({ mutableListOf() }, MAX_SIZE), ByteBufUtils::readVec2d)
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    if (points.size < 3) return

    setupFlat()

    val size = points.size
    val a = points[0]

    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix

    buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)

    for (i in 1 until size - 1) {
      val b = points[i]
      val c = points[i + 1]
      buffer.vertex(matrix, a.x.toFloat(), a.y.toFloat(), 0f).color(red, green, blue, alpha).next()
      buffer.vertex(matrix, b.x.toFloat(), b.y.toFloat(), 0f).color(red, green, blue, alpha).next()
      buffer.vertex(matrix, c.x.toFloat(), c.y.toFloat(), 0f).color(red, green, blue, alpha).next()
    }

    BufferRenderer.drawWithShader(buffer.end())
  }
}
