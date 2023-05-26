package io.sc3.plethora.integration.computercraft.meta.item

import dan200.computercraft.impl.MediaProviders
import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.item.ItemStack

class MediaItemMeta : BasicMetaProvider<ItemStack>() {
  override fun getMeta(target: ItemStack): Map<String, *> {
    val media = MediaProviders.get(target) ?: return emptyMap<String, Any>()

    return mapOf(
      "media" to mapOf(
        "label"       to media.getLabel(target),
        "recordTitle" to media.getAudioTitle(target),
        "recordName"  to media.getAudio(target)?.id?.toString()
      )
    )
  }
}
