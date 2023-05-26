package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.ArgumentExt.getVec2d
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.method.toResult
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import io.sc3.plethora.util.Vec2d

/**
 * An object which can be positioned in 2D.
 */
interface Positionable2d {
  var position: Vec2d

  companion object {
    val GET_POSITION = BasicMethod.of(
      "getPosition", "function():number, number -- Get the position for this object.",
      { unbaked, _ -> getPosition(unbaked) }, false
    )
    private fun getPosition(unbaked: IUnbakedContext<Positionable2d>) =
      safeFromTarget(unbaked).position.toResult()

    val SET_POSITION = BasicMethod.of(
      "setPosition", "function(number, number) -- Set the position for this object.",
      { unbaked, args -> setPosition(unbaked, args) }, false
    )
    private fun setPosition(unbaked: IUnbakedContext<Positionable2d>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).position = args.getVec2d(0)
      return FutureMethodResult.empty()
    }
  }
}
