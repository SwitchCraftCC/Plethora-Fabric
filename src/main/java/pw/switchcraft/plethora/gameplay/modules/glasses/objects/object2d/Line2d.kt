package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d

import com.google.common.base.Objects
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.LINE_2D
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable
import pw.switchcraft.plethora.util.ByteBufUtils
import pw.switchcraft.plethora.util.DirtyingProperty
import pw.switchcraft.plethora.util.Vec2d
import javax.annotation.Nonnull

class Line2d(
  id: Int,
  parent: Int
) : ColourableObject(id, parent, LINE_2D), Scalable, MultiPoint2d {
  private var start = Vec2d.ZERO
  private var end = Vec2d.ZERO

  /** Line thickness */
  override var scale by DirtyingProperty(1f)

  @Nonnull
  override fun getPoint(idx: Int): Vec2d = if (idx == 0) start else end

  override fun setVertex(idx: Int, @Nonnull point: Vec2d) {
    if (idx == 0) {
      if (!Objects.equal(start, point)) {
        start = point
        setDirty()
      }
    } else {
      if (!Objects.equal(end, point)) {
        end = point
        setDirty()
      }
    }
  }

  override val vertices: Int
    get() = 2

  override fun writeInitial(@Nonnull buf: PacketByteBuf) {
    super.writeInitial(buf)
    ByteBufUtils.writeVec2d(buf, start)
    ByteBufUtils.writeVec2d(buf, end)
    buf.writeFloat(scale)
  }

  override fun readInitial(@Nonnull buf: PacketByteBuf) {
    super.readInitial(buf)
    start = ByteBufUtils.readVec2d(buf)
    end = ByteBufUtils.readVec2d(buf)
    scale = buf.readFloat()
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    setupFlat()
    RenderSystem.lineWidth(scale)

    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix
    val normal = matrices.peek().normalMatrix

    RenderSystem.setShader { GameRenderer.getRenderTypeLinesShader() }

    buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)
    buffer
      .vertex(matrix, start.x.toFloat(), start.y.toFloat(), 0f)
      .color(red, green, blue, alpha)
      .normal(normal, 0f, 1f, 0f).next()
    buffer
      .vertex(matrix, end.x.toFloat(), end.y.toFloat(), 0f)
      .color(red, green, blue, alpha)
      .normal(normal, 0f, 1f, 0f).next()

    BufferRenderer.drawWithShader(buffer.end())
    RenderSystem.lineWidth(1f)
  }
}
