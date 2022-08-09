package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d

import dan200.computercraft.api.lua.IArguments
import pw.switchcraft.plethora.api.method.ArgumentHelper.assertBetween
import pw.switchcraft.plethora.api.method.BasicMethod
import pw.switchcraft.plethora.api.method.FutureMethodResult
import pw.switchcraft.plethora.api.method.IUnbakedContext
import pw.switchcraft.plethora.api.method.getVec2d
import pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget
import pw.switchcraft.plethora.util.Vec2d

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
      val idx = assertBetween(args.getInt(0), 1, obj.vertices, "Index out of range (%s)")
      val point = obj.getPoint(idx - 1)
      return FutureMethodResult.result(point.x(), point.y())
    }

    val SET_POINT = BasicMethod.of(
      "setPoint", "function(idx:int, x:number, y:number) -- Set the specified vertex of this object.",
      { unbaked, args -> setPoint(unbaked, args) }, false
    )
    private fun setPoint(unbaked: IUnbakedContext<MultiPoint2d>, args: IArguments): FutureMethodResult {
      val obj = safeFromTarget(unbaked)
      val idx = assertBetween(args.getInt(0), 1, obj.vertices, "Index out of range (%s)")
      obj.setVertex(idx - 1, args.getVec2d(1))
      return FutureMethodResult.empty()
    }
  }
}
