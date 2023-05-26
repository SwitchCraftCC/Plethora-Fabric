package io.sc3.plethora.integration.vanilla.meta.block

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.block.Block
import net.minecraft.registry.Registries

object BlockMeta : BasicMetaProvider<Block>(
  description = "Provide the registry name, display name and translation key of a block."
) {
  override fun getMeta(target: Block): Map<String, *> {
    val name = Registries.BLOCK.getId(target)
    return mapOf(
      "name"           to name.toString(),
      "displayName"    to target.name.string,
      "translationKey" to target.translationKey
    )
  }
}
