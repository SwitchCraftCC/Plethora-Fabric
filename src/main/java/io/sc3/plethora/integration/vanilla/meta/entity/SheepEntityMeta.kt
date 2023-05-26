package io.sc3.plethora.integration.vanilla.meta.entity

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.entity.passive.SheepEntity

object SheepEntityMeta : BasicMetaProvider<SheepEntity>(
  description = "Provides the wool colour of the sheep."
) {
  override fun getMeta(target: SheepEntity): Map<String, *> {
    val color = target.color.getName()
    return mapOf(
      "woolColour" to color,
      "woolColor"  to color
    )
  }
}
