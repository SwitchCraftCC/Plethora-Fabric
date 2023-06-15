package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import com.google.common.base.Objects
import com.mojang.blaze3d.systems.RenderSystem
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.objects.ColourableObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.LINE_2D
import io.sc3.plethora.gameplay.modules.glasses.objects.Scalable
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.DirtyingProperty
import io.sc3.plethora.util.Vec2d
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.network.PacketByteBuf
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
  override fun draw(canvas: CanvasClient, ctx: DrawContext, consumers: VertexConsumerProvider?) {
    setupFlat()
    RenderSystem.lineWidth(scale)

    val matrices = ctx.matrices
    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix
    val normal = matrices.peek().normalMatrix

    RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)

    buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)
    buffer
      .vertex(matrix, start.x.toFloat(), start.y.toFloat(), 0f)
      .color(red, green, blue, alpha)
      .normal(normal, 0f, 1f, 0f).next()
    buffer
      .vertex(matrix, end.x.toFloat(), end.y.toFloat(), 0f)
      .color(red, green, blue, alpha)
      .normal(normal, 0f, 1f, 0f).next()

    BufferRenderer.drawWithGlobalProgram(buffer.end())
    RenderSystem.lineWidth(1f)
  }
}
