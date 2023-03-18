package io.sc3.plethora.integration.scperipherals

import io.sc3.peripherals.posters.PosterItem
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

class PosterItemMeta : ItemStackMetaProvider<PosterItem>(PosterItem::class.java) {
  override fun getMeta(stack: ItemStack, item: PosterItem): Map<String, *> {
    val poster = PosterItem.printData(stack) ?: return emptyMap<String, Any?>()
    return mapOf(
      "tooltip"  to poster.tooltip, // Or `null`
      "posterId" to poster.posterId,
    )
  }
}
