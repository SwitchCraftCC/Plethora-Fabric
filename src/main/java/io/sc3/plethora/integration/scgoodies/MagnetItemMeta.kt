package io.sc3.plethora.integration.scgoodies

import io.sc3.goodies.itemmagnet.ItemMagnetItem
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

class MagnetItemMeta : ItemStackMetaProvider<ItemMagnetItem>(ItemMagnetItem::class.java) {
  override fun getMeta(stack: ItemStack, item: ItemMagnetItem): Map<String, *> {
    return mapOf(
      "magnetLevel" to ItemMagnetItem.stackLevel(stack),
      "magnetRadius" to ItemMagnetItem.stackRadius(stack),
      "magnetEnabled" to ItemMagnetItem.stackEnabled(stack),
      "magnetBlocked" to ItemMagnetItem.stackBlocked(stack),
      // The other useful properties of the magnet, `damage` and `maxDamage`, are provided by the basic item meta
    )
  }
}
