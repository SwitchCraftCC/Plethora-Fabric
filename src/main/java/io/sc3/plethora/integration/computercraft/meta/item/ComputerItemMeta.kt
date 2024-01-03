package io.sc3.plethora.integration.computercraft.meta.item

import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.computer.items.IComputerItem
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import io.sc3.plethora.gameplay.registry.Registration.ModItems
import net.minecraft.item.ItemStack

class ComputerItemMeta : ItemStackMetaProvider<IComputerItem>(IComputerItem::class.java, "computer") {
  override fun getMeta(stack: ItemStack, item: IComputerItem): Map<String, *> {
    val data: MutableMap<String, Any?> = HashMap(3)

    val id = item.getComputerID(stack)
    if (id >= 0) data["id"] = id

    val label = item.getLabel(stack)
    if (!label.isNullOrEmpty()) data["label"] = label

    // "family" used to be available on IComputerItem. Instead, we just look it up based on the current item.
    data["family"] = when (item) {
      ModRegistry.Items.COMPUTER_NORMAL.get(), ModRegistry.Items.TURTLE_NORMAL.get(), ModRegistry.Items.POCKET_COMPUTER_NORMAL.get() -> "normal"
      ModRegistry.Items.COMPUTER_ADVANCED.get(), ModRegistry.Items.TURTLE_ADVANCED.get(), ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get(), ModItems.NEURAL_INTERFACE -> "advanced"
      ModRegistry.Items.COMPUTER_COMMAND.get() -> "command"
      else -> "unknown"
    }

    return data
  }
}
