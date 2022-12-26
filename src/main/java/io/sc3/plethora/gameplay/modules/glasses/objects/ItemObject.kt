package io.sc3.plethora.gameplay.modules.glasses.objects

import dan200.computercraft.api.lua.IArguments
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import io.sc3.plethora.gameplay.modules.glasses.GlassesArgumentHelper

/**
 * An object which contains an item.
 */
interface ItemObject {
  var item: Item

  companion object {
    val GET_ITEM = BasicMethod.of(
      "getItem", "function():string -- Get the item for this object.",
      { unbaked, _ -> getItem(unbaked) }, false
    )
    private fun getItem(unbaked: IUnbakedContext<ItemObject>): FutureMethodResult {
      val item = safeFromTarget(unbaked).item
      val id = Registries.ITEM.getId(item)
      return FutureMethodResult.result(id.toString())
    }

    val SET_ITEM = BasicMethod.of(
      "setItem", "function(string) -- Set the item for this object.",
      { unbaked, args -> setItem(unbaked, args) }, false
    )
    private fun setItem(unbaked: IUnbakedContext<ItemObject>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).item = GlassesArgumentHelper.getItem(args, 0)
      return FutureMethodResult.empty()
    }
  }
}
