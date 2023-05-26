package io.sc3.plethora.gameplay.modules

import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

object BindableModuleItemMeta : ItemStackMetaProvider<BindableModuleItem>(BindableModuleItem::class.java) {
  override fun getMeta(stack: ItemStack, item: BindableModuleItem) =
    ModuleContextHelpers.getProfile(stack)?.let {
      mapOf(
        "bound" to mapOf(
          "id"   to it.id?.toString(),
          "name" to it.name,
        )
      )
    } ?: emptyMap<String, Any>()
}
