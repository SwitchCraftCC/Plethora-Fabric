package io.sc3.plethora.gameplay.modules.glasses.objects

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.ArgumentExt.assertStringLength
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import io.sc3.plethora.gameplay.modules.glasses.objects.object2d.Text2d

/**
 * An object which contains text.
 */
interface TextObject {
  var text: String
  var dropShadow: Boolean
  var lineHeight: Short

  companion object {
    val GET_TEXT = BasicMethod.of(
      "getText", "function():string -- Get the text for this object.",
      { unbaked, _ -> getText(unbaked) }, false
    )
    private fun getText(unbaked: IUnbakedContext<TextObject>) =
      FutureMethodResult.result(safeFromTarget(unbaked).text)

    val SET_TEXT = BasicMethod.of(
      "setText", "function(string) -- Set the text for this object.",
      { unbaked, args -> setText(unbaked, args) }, false
    )
    private fun setText(unbaked: IUnbakedContext<TextObject>, args: IArguments): FutureMethodResult {
      val contents = args.assertStringLength(0, 0, Text2d.MAX_LENGTH)
      safeFromTarget(unbaked).text = contents
      return FutureMethodResult.empty()
    }

    val SET_SHADOW = BasicMethod.of(
      "setShadow", "function(boolean) -- Set the shadow for this object.",
      { unbaked, args -> setShadow(unbaked, args) }, false
    )
    private fun setShadow(unbaked: IUnbakedContext<TextObject>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).dropShadow = args.getBoolean(0)
      return FutureMethodResult.empty()
    }

    val HAS_SHADOW = BasicMethod.of(
      "hasShadow", "function():boolean -- Get the shadow for this object.",
      { unbaked, _ -> hasShadow(unbaked) }, false
    )
    private fun hasShadow(unbaked: IUnbakedContext<TextObject>) =
      FutureMethodResult.result(safeFromTarget(unbaked).dropShadow)

    val GET_LINE_HEIGHT = BasicMethod.of(
      "getLineHeight", "function():number -- Get the line height for this object.",
      { unbaked, _ -> getLineHeight(unbaked) }, false
    )
    private fun getLineHeight(unbaked: IUnbakedContext<TextObject>) =
      FutureMethodResult.result(safeFromTarget(unbaked).lineHeight)

    val SET_LINE_HEIGHT = BasicMethod.of(
      "setLineHeight", "function(number) -- Set the line height for this object.",
      { unbaked, args -> setLineHeight(unbaked, args) }, false
    )
    private fun setLineHeight(unbaked: IUnbakedContext<TextObject>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).lineHeight = args.getInt(0).toShort()
      return FutureMethodResult.empty()
    }
  }
}
