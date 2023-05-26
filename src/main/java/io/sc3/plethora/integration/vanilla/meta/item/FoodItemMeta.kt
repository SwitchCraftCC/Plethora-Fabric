package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.item.ItemStack

object FoodItemMeta : BasicMetaProvider<ItemStack>(
  description = "Provides the hunger and saturation this foodstuff restores."
) {
  override fun getMeta(target: ItemStack): Map<String, Any> {
    val food = target.item.foodComponent ?: return emptyMap()
    return mapOf(
      "hunger"     to food.hunger,
      "saturation" to food.saturationModifier
    )
  }
}
