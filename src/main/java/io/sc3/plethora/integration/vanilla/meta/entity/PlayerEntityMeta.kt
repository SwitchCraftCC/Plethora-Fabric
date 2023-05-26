package io.sc3.plethora.integration.vanilla.meta.entity

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.entity.player.PlayerEntity

object PlayerEntityMeta : BasicMetaProvider<PlayerEntity>() {
  override fun getMeta(target: PlayerEntity): Map<String, *> {
    val h = target.hungerManager
    return mapOf(
      "food" to mapOf(
        "hunger"     to h.foodLevel,
        "saturation" to h.saturationLevel,
        "hungry"     to h.isNotFull
      ),
      "heldItemSlot" to target.inventory.selectedSlot
    )
  }
}
