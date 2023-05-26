package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.ArgumentExt.assertIntBetween
import io.sc3.plethora.api.method.ArgumentExt.getVec2d
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import io.sc3.plethora.util.Vec2d

/**
 * A polygon for which you can set multiple points.
 */
interface MultiPoint2d {
  fun getPoint(idx: Int): Vec2d
  fun setVertex(idx: Int, point: Vec2d)
  val vertices: Int

  companion object {
    val GET_POINT = BasicMethod.of(
      "getPoint", "function(idx:int):number, number -- Get the specified vertex of this object.",
      { unbaked, args -> getPoint(unbaked, args) }, false
    )
    private fun getPoint(unbaked: IUnbakedContext<MultiPoint2d>, args: IArguments): FutureMethodResult {
      val obj = safeFromTarget(unbaked)
      val idx = args.assertIntBetween(0, 1, obj.vertices, "Index out of range (%s)")
      val point = obj.getPoint(idx - 1)
      return FutureMethodResult.result(point.x(), point.y())
    }

    val SET_POINT = BasicMethod.of(
      "setPoint", "function(idx:int, x:number, y:number) -- Set the specified vertex of this object.",
      { unbaked, args -> setPoint(unbaked, args) }, false
    )
    private fun setPoint(unbaked: IUnbakedContext<MultiPoint2d>, args: IArguments): FutureMethodResult {
      val obj = safeFromTarget(unbaked)
      val idx = args.assertIntBetween(0, 1, obj.vertices, "Index out of range (%s)")
      obj.setVertex(idx - 1, args.getVec2d(1))
      return FutureMethodResult.empty()
    }
  }
}
