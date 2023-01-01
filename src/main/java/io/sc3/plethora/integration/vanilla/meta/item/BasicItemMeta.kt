package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.item.ItemStack

class BasicItemMeta : BasicMetaProvider<ItemStack>() {
  override fun getMeta(stack: ItemStack): Map<String, *> {
    if (stack.isEmpty) return emptyMap<String, Any>()

    val data = HashMap<String, Any?>()
    data["rawName"] = stack.translationKey
    return data
  }
}
