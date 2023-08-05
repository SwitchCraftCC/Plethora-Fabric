package io.sc3.plethora.integration

import dan200.computercraft.api.detail.DetailRegistry
import dan200.computercraft.api.detail.VanillaDetailRegistries
import io.sc3.plethora.api.meta.BaseMetaProvider
import io.sc3.plethora.api.method.IPartialContext
import io.sc3.plethora.api.reference.ConstantReference
import net.minecraft.item.ItemStack

/** Wraps an object so that `getMetadata` returns details from a [DetailRegistry]. */
class DetailsMetaWrapper<T : Any>(
  private val registry: DetailRegistry<T>, val value: T
) : ConstantReference<DetailsMetaWrapper<T>> {
  override fun get(): DetailsMetaWrapper<T> = this
  override fun safeGet(): DetailsMetaWrapper<T> = this

  private fun details() = registry.getDetails(value)

  object MetaProvider : BaseMetaProvider<DetailsMetaWrapper<*>>(
    description = "Simply wraps an object and exposes metadata for that. You can happily ignore this."
  ) {
    override fun getMeta(context: IPartialContext<DetailsMetaWrapper<*>>): Map<String, *> = context.target.details()
  }

  companion object {
    @JvmStatic
    fun stack(value: ItemStack): DetailsMetaWrapper<ItemStack> =
      DetailsMetaWrapper(VanillaDetailRegistries.ITEM_STACK, value)
  }
}
