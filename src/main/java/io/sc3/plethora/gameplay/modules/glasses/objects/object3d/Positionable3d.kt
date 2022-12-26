package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d

import dan200.computercraft.api.lua.IArguments
import net.minecraft.util.math.Vec3d
import pw.switchcraft.plethora.api.method.*
import pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget

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
