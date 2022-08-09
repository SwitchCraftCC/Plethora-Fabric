package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d

import dan200.computercraft.api.lua.IArguments
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import pw.switchcraft.plethora.api.method.BasicMethod
import pw.switchcraft.plethora.api.method.FutureMethodResult
import pw.switchcraft.plethora.api.method.IUnbakedContext
import pw.switchcraft.plethora.api.method.getVec2d
import pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.RECTANGLE_2D
import pw.switchcraft.plethora.util.ByteBufUtils
import pw.switchcraft.plethora.util.DirtyingProperty
import pw.switchcraft.plethora.util.Vec2d

class Rectangle2d(
  id: Int,
  parent: Int
) : ColourableObject(id, parent, RECTANGLE_2D), Positionable2d {
  override var position by DirtyingProperty(Vec2d.ZERO!!)

  private var width: Double = 0.0
  private var height: Double = 0.0

  var size
    get() = Vec2d(width, height)
    set(value) {
      if (value != size) {
        width = value.x
        height = value.y
        setDirty()
      }
    }

  override fun writeInitial(buf: PacketByteBuf) {
    super.writeInitial(buf)
    ByteBufUtils.writeVec2d(buf, position)
    buf.writeDouble(width)
    buf.writeDouble(height)
  }

  override fun readInitial(buf: PacketByteBuf) {
    super.readInitial(buf)
    position = ByteBufUtils.readVec2d(buf)
    width = buf.readDouble()
    height = buf.readDouble()
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    setupFlat()

    val minX = position.x().toFloat(); val minY = position.y().toFloat()
    val maxX = (minX + width).toFloat(); val maxY = (minY + height).toFloat()

    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
    buffer.vertex(matrix, minX, minY, 0.0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, maxY, 0.0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, maxY, 0.0f).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, minY, 0.0f).color(red, green, blue, alpha).next()
    BufferRenderer.drawWithShader(buffer.end())
  }

  companion object {
    val GET_SIZE = BasicMethod.of(
      "getSize", "function():number, number -- Get the size of this rectangle.",
      { unbaked, _ -> getSize(unbaked) }, false
    )
    private fun getSize(unbaked: IUnbakedContext<Rectangle2d>): FutureMethodResult {
      val rect = safeFromTarget(unbaked)
      return FutureMethodResult.result(rect.width, rect.height)
    }

    val SET_SIZE = BasicMethod.of(
      "setSize", "function(number, number) -- Set the size of this rectangle.",
      { unbaked, args -> setSize(unbaked, args) }, false
    )
    private fun setSize(unbaked: IUnbakedContext<Rectangle2d>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).size = args.getVec2d(0)
      return FutureMethodResult.empty()
    }
  }
}
