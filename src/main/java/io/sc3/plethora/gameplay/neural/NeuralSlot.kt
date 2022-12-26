package io.sc3.plethora.gameplay.neural

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class NeuralSlot(inv: Inventory, i: Int, x: Int, y: Int) : Slot(inv, i, x, y) {
  override fun canInsert(stack: ItemStack): Boolean {
    return !stack.isEmpty && inventory.isValid(index, stack)
  }
}
