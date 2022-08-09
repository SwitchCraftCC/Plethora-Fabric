package pw.switchcraft.plethora.gameplay.modules.glasses.objects

import dan200.computercraft.api.lua.IArguments
import pw.switchcraft.plethora.api.method.BasicMethod
import pw.switchcraft.plethora.api.method.FutureMethodResult
import pw.switchcraft.plethora.api.method.IUnbakedContext
import pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget

const val DEFAULT_COLOUR = 0xFFFFFFFFL

/**
 * An object which can be coloured.
 */
interface Colourable {
  var colour: Int

  companion object {
    val GET_COLOUR = BasicMethod.of(
      "getColour", "function():number -- Get the colour for this object.",
      { unbaked, _ -> getColour(unbaked) }, false
    )
    val GET_COLOR = BasicMethod.alias(GET_COLOUR, "getColor")
    private fun getColour(unbaked: IUnbakedContext<Colourable>) =
      FutureMethodResult.result(safeFromTarget(unbaked).colour.toLong() and 0xFFFFFFFFL)

    val SET_COLOUR = BasicMethod.of(
      "setColour", "function(colour|r:int, [g:int, b:int], [alpha:int]):number -- Set the colour for this object.",
      { unbaked, args -> setColour(unbaked, args) }, false
    )
    val SET_COLOR = BasicMethod.alias(SET_COLOUR, "setColor")
    private fun setColour(unbaked: IUnbakedContext<Colourable>?, args: IArguments): FutureMethodResult {
      val obj = safeFromTarget(unbaked)
      when (args.count()) {
        1 -> obj.colour = args.getInt(0)
        3 -> {
          val r = args.getInt(0) and 0xFF
          val g = args.getInt(1) and 0xFF
          val b = args.getInt(2) and 0xFF
          obj.colour = r shl 24 or (g shl 16) or (b shl 8) or (obj.colour and 0xFF)
        }
        else -> {
          val r = args.getInt(0) and 0xFF
          val g = args.getInt(1) and 0xFF
          val b = args.getInt(2) and 0xFF
          val a = args.getInt(3) and 0xFF
          obj.colour = r shl 24 or (g shl 16) or (b shl 8) or a
        }
      }
      return FutureMethodResult.empty()
    }

    val GET_ALPHA = BasicMethod.of(
      "getAlpha", "function():number -- Get the alpha for this object.",
      { unbaked, _ -> getAlpha(unbaked) }, false
    )
    private fun getAlpha(unbaked: IUnbakedContext<Colourable>) =
      FutureMethodResult.result(safeFromTarget(unbaked).colour and 0xFF)

    val SET_ALPHA = BasicMethod.of(
      "setAlpha", "function(alpha:number):number -- Set the alpha for this object.",
      { unbaked, args -> setAlpha(unbaked, args) }, false
    )
    private fun setAlpha(unbaked: IUnbakedContext<Colourable>, args: IArguments): FutureMethodResult {
      val obj = safeFromTarget(unbaked)
      obj.colour = obj.colour and 0xFF.inv() or (args.getInt(0) and 0xFF)
      return FutureMethodResult.empty()
    }
  }
}
