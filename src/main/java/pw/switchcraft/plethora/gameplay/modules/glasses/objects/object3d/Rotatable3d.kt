package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d

import dan200.computercraft.api.lua.IArguments
import net.minecraft.util.math.Vec3d
import pw.switchcraft.plethora.api.method.*
import pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget

interface Rotatable3d {
  var rotation: Vec3d?

  companion object {
    val GET_ROTATION = BasicMethod.of(
      "getRotation", "function():nil|number, number, number -- Get the rotation for this object, or nil if it faces the player.",
      { unbaked, _ -> getRotation(unbaked) }, false
    )
    private fun getRotation(unbaked: IUnbakedContext<Rotatable3d>) =
      safeFromTarget(unbaked).rotation.toResult()

    val SET_ROTATION = BasicMethod.of(
      "setRotation", "function([x:number, y:number, z:number]) -- Set the rotation for this object, passing nothing if it should face the player.",
      { unbaked, args -> setRotation(unbaked, args) }, false
    )
    private fun setRotation(unbaked: IUnbakedContext<Rotatable3d>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).rotation = args.getVec3dNullable(0)
      return FutureMethodResult.empty()
    }
  }
}
