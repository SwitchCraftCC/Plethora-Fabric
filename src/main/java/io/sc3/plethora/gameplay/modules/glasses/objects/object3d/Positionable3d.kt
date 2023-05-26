package io.sc3.plethora.gameplay.modules.glasses.objects.object3d

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.ArgumentExt.getVec3d
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.method.toResult
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import net.minecraft.util.math.Vec3d

/**
 * An object which can be positioned in 3D.
 */
interface Positionable3d {
  var position: Vec3d

  companion object {
    val GET_POSITION = BasicMethod.of(
      "getPosition", "function():number, number, number -- Get the position for this object.",
      { unbaked, _ -> getPosition(unbaked) }, false
    )
    private fun getPosition(unbaked: IUnbakedContext<Positionable3d>) =
      safeFromTarget(unbaked).position.toResult()

    val SET_POSITION = BasicMethod.of(
      "setPosition", "function(number, number, number) -- Set the position for this object.",
      { unbaked, args -> setPosition(unbaked, args) }, false
    )
    private fun setPosition(unbaked: IUnbakedContext<Positionable3d>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).position = args.getVec3d(0)
      return FutureMethodResult.empty()
    }
  }
}
