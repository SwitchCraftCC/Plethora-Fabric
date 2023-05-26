package io.sc3.plethora.gameplay.modules.glasses.objects.object3d

import com.mojang.blaze3d.systems.RenderSystem
import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.ArgumentExt.getVec3d
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.method.toResult
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.objects.ColourableObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.BOX_3D
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.DirtyingProperty
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

class Box3d(
  id: Int,
  parent: Int
) : ColourableObject(id, parent, BOX_3D), Positionable3d, DepthTestable {
  override var position by DirtyingProperty(Vec3d.ZERO!!)
  override var hasDepthTest by DirtyingProperty(true)

  private var width: Double = 0.0
  private var height: Double = 0.0
  private var depth: Double = 0.0

  var size
    get() = Vec3d(width, height, depth)
    set(value) {
      if (value != size) {
        width = value.x
        height = value.y
        depth = value.z
        setDirty()
      }
    }

  override fun readInitial(buf: PacketByteBuf) {
    super.readInitial(buf)
    position = ByteBufUtils.readVec3d(buf)
    width = buf.readDouble()
    height = buf.readDouble()
    depth = buf.readDouble()
    hasDepthTest = buf.readBoolean()
  }

  override fun writeInitial(buf: PacketByteBuf) {
    super.writeInitial(buf)
    ByteBufUtils.writeVec3d(buf, position)
    buf.writeDouble(width)
    buf.writeDouble(height)
    buf.writeDouble(depth)
    buf.writeBoolean(hasDepthTest)
  }

  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    setupFlat()

    if (hasDepthTest) {
      RenderSystem.enableDepthTest()
    } else {
      RenderSystem.disableDepthTest()
    }

    val minX = position.x.toFloat(); val minY = position.y.toFloat(); val minZ = position.z.toFloat()
    val maxX = (minX + width).toFloat(); val maxY = (minY + height).toFloat(); val maxZ = (minZ + depth).toFloat()

    val buffer = Tessellator.getInstance().buffer
    val matrix = matrices.peek().positionMatrix

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)

    // Down
    buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next()

    // Up
    buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next()

    // North
    buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next()

    // South
    buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next()

    // East
    buffer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next()

    // West
    buffer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next()
    buffer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next()

    BufferRenderer.drawWithGlobalProgram(buffer.end())
  }

  companion object {
    val GET_SIZE = BasicMethod.of(
      "getSize", "function():number, number, number -- Get the size of this box.",
      { unbaked, _ -> getSize(unbaked) }, false
    )
    private fun getSize(unbaked: IUnbakedContext<Box3d>): FutureMethodResult =
      safeFromTarget(unbaked).size.toResult()

    val SET_SIZE = BasicMethod.of(
      "setSize", "function(number, number, number) -- Set the size of this box.",
      { unbaked, args -> setSize(unbaked, args) }, false
    )
    private fun setSize(unbaked: IUnbakedContext<Box3d>, args: IArguments): FutureMethodResult {
      val vec = args.getVec3d(0)
      safeFromTarget(unbaked).size = vec
      return FutureMethodResult.empty()
    }
  }
}
