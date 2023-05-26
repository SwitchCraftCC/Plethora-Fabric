package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.item.ItemStack

object BasicItemMeta : BasicMetaProvider<ItemStack>() {
  override fun getMeta(target: ItemStack): Map<String, *> {
    if (target.isEmpty) return emptyMap<String, Any>()
    return mapOf("rawName" to target.translationKey)
  }
}
