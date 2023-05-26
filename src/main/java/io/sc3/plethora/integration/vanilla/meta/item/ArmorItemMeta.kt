package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ArmorItem
import net.minecraft.item.DyeableArmorItem
import net.minecraft.item.ItemStack

/**
 * Meta provider for amour properties. Material is handled in [ItemMaterialMeta].
 */
object ArmorItemMeta : ItemStackMetaProvider<ArmorItem>(ArmorItem::class.java, "Provides type and colour of amour.") {
  override fun getMeta(stack: ItemStack, item: ArmorItem): Map<String, *> {
    val data = mutableMapOf<String, Any>(
      "armorType" to item.slotType.getName()
    )

    if (item is DyeableArmorItem) {
      val color = item.getColor(stack)
      if (color >= 0) {
        data["color"] = color
        data["colour"] = color
      }
    }

    return data
  }
}
