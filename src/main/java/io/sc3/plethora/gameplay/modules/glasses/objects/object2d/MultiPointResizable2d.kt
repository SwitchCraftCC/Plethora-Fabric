package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import io.sc3.plethora.api.method.ArgumentExt.assertIntBetween
import io.sc3.plethora.api.method.ArgumentExt.getVec2d
import io.sc3.plethora.api.method.ArgumentExt.getVec2dTable
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import io.sc3.plethora.util.Vec2d

const val MAX_SIZE = 255

interface MultiPointResizable2d : MultiPoint2d {
  fun removePoint(idx: Int)
  fun addPoint(idx: Int, point: Vec2d)

  fun addPointsFromArgs(args: IArguments, optArgCount: Int = 0): Int {
    var i = 0
    while (i < args.count()) {
      val arg = args[i]
      if (i >= args.count() - optArgCount && arg is Number) {
        break
      } else {
        addPoint(i, args.getVec2dTable(i))
      }
      i++
    }
    return i
  }

  companion object {
    val GET_POINT_COUNT = BasicMethod.of(
      "getPointCount", "function():number -- Get the number of vertices on this object.",
      { unbaked, _ -> getPointCount(unbaked) }, false
    )
    private fun getPointCount(unbaked: IUnbakedContext<MultiPointResizable2d>) =
      FutureMethodResult.result(safeFromTarget(unbaked).vertices)

    val REMOVE_POINT = BasicMethod.of(
      "removePoint", "function(idx:int) -- Remove the specified vertex of this object.",
      { unbaked, args -> removePoint(unbaked, args) }, false
    )
    private fun removePoint(unbaked: IUnbakedContext<MultiPointResizable2d>, args: IArguments): FutureMethodResult {
      val obj = safeFromTarget(unbaked)
      val idx = args.assertIntBetween(0, 1, obj.vertices, "Index out of range (%s)")
      obj.removePoint(idx - 1)
      return FutureMethodResult.empty()
    }

    val INSERT_POINT = BasicMethod.of(
      "insertPoint", "function([idx:int, ]x:number, y:number) -- Add a specified vertex to this object.",
      { unbaked, args -> insertPoint(unbaked, args) }, false
    )
    private fun insertPoint(unbaked: IUnbakedContext<MultiPointResizable2d>, args: IArguments): FutureMethodResult {
      val obj = safeFromTarget(unbaked)
      if (obj.vertices > MAX_SIZE) throw LuaException("Too many vertices")

      val idx: Int
      val pos: Vec2d
      if (args.count() >= 3) {
        idx = args.assertIntBetween(0, 1, obj.vertices, "Index out of range (%s)")
        pos = args.getVec2d(1)
      } else {
        idx = obj.vertices
        pos = args.getVec2d(2)
      }

      obj.addPoint(idx - 1, pos)
      return FutureMethodResult.empty()
    }
  }
}
