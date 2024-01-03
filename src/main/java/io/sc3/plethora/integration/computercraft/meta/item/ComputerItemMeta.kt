package io.sc3.plethora.integration.computercraft.meta.item

import dan200.computercraft.shared.computer.items.IComputerItem
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

class ComputerItemMeta : ItemStackMetaProvider<IComputerItem>(IComputerItem::class.java, "computer") {
  override fun getMeta(stack: ItemStack, item: IComputerItem): Map<String, *> {
    val data: MutableMap<String, Any?> = HashMap(3)

    val id = item.getComputerID(stack)
    if (id >= 0) data["id"] = id

    val label = item.getLabel(stack)
    if (!label.isNullOrEmpty()) data["label"] = label

    return data
  }
}
