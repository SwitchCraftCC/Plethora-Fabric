package io.sc3.plethora.gameplay.modules.glasses.objects

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.core.ContextHelpers.safeFromTarget

/**
 * An object which can be scaled. This includes point side, text size and line thickness.
 */
interface Scalable {
  var scale: Float

  companion object {
    val GET_SCALE = BasicMethod.of(
      "getScale", "function():number -- Get the scale for this object.",
      { unbaked, _ -> getScale(unbaked) }, false
    )
    private fun getScale(unbaked: IUnbakedContext<Scalable>?) =
      FutureMethodResult.result(safeFromTarget(unbaked).scale)

    val SET_SCALE = BasicMethod.of(
      "setScale", "function(number) -- Set the scale for this object.",
      { unbaked, args -> setScale(unbaked, args) }, false
    )
    private fun setScale(unbaked: IUnbakedContext<Scalable>, args: IArguments): FutureMethodResult {
      val scale = args.getDouble(0).toFloat()
      if (scale <= 0) throw LuaException("Scale must be > 0")
      safeFromTarget(unbaked).scale = scale
      return FutureMethodResult.empty()
    }
  }
}
