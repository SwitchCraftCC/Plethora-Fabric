package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d

import dan200.computercraft.api.lua.IArguments
import pw.switchcraft.plethora.api.method.BasicMethod
import pw.switchcraft.plethora.api.method.FutureMethodResult
import pw.switchcraft.plethora.api.method.IUnbakedContext
import pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget

interface DepthTestable {
  var hasDepthTest: Boolean

  companion object {
    val IS_DEPTH_TESTED = BasicMethod.of(
      "isDepthTested", "function():boolean -- Determine whether depth testing is enabled for this object.",
      { unbaked, _ -> isDepthTested(unbaked) }, false
    )
    private fun isDepthTested(unbaked: IUnbakedContext<DepthTestable>) =
      FutureMethodResult.result(safeFromTarget(unbaked).hasDepthTest)

    val SET_DEPTH_TESTED = BasicMethod.of(
      "setDepthTested", "function(boolean) -- Set whether depth testing is enabled for this object.",
      { unbaked, args -> setDepthTested(unbaked, args) }, false
    )
    private fun setDepthTested(unbaked: IUnbakedContext<DepthTestable>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).hasDepthTest = args.getBoolean(0)
      return FutureMethodResult.empty()
    }
  }
}
