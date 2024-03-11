package io.sc3.plethora.integration.vanilla.meta.blockentity

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.block.entity.BeehiveBlockEntity

object BeehiveBlockEntityMeta : BasicMetaProvider<BeehiveBlockEntity>(
  description = "Provides information about the beehive."
) {
  override fun getMeta(target: BeehiveBlockEntity) =
    mapOf(
      "beeCount" to target.beeCount,
      "honeyLevel" to BeehiveBlockEntity.getHoneyLevel(target.cachedState)
    )
}
