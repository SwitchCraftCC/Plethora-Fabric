package io.sc3.plethora.integration.vanilla.meta.entity

import dan200.computercraft.api.detail.VanillaDetailRegistries
import io.sc3.plethora.api.meta.BaseMetaProvider
import io.sc3.plethora.api.method.IPartialContext
import net.minecraft.entity.ItemEntity

object ItemEntityMeta : BaseMetaProvider<ItemEntity>(
  description = "Provides the stack of a dropped item."
) {
  override fun getMeta(context: IPartialContext<ItemEntity>): Map<String, *> {
    val stack = context.target.stack
    return mapOf(
      "item" to VanillaDetailRegistries.ITEM_STACK.getDetails(stack)
    )
  }
}
