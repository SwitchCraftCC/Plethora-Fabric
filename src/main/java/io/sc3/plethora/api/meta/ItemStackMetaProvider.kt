package io.sc3.plethora.api.meta

import net.minecraft.item.ItemStack
import java.util.*

/**
 * A meta provider for [ItemStack]s whose item has a specific type.
 *
 * @param <T> The type the stack's item must have.
 */
abstract class ItemStackMetaProvider<T> @JvmOverloads constructor(
  val type: Class<T>,
  private val namespace: String? = null,
  priority: Int = 0,
  description: String? = null
) : BasicMetaProvider<ItemStack>(priority, description) {
  override fun getMeta(target: ItemStack): Map<String, *> {
    val item = target.item
    if (!type.isInstance(item)) return emptyMap<String, Any>()

    val child = getMeta(target, type.cast(item))
    return if (namespace == null || child.isEmpty()) {
      child
    } else {
      Collections.singletonMap(namespace, child)
    }
  }

  abstract fun getMeta(stack: ItemStack, item: T): Map<String, *>
}
