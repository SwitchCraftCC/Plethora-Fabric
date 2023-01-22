package io.sc3.plethora.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemStack.EMPTY

class RangedInventoryWrapper(
  private val underlying: Inventory,
  private val minSlot: Int,
  private val size: Int,
  val allowConsumption: Boolean = true
) : Inventory {
  private val maxSlot = minSlot + size

  override fun size() = size
  override fun isEmpty() = false
  override fun getMaxCountPerStack() = underlying.maxCountPerStack

  override fun canPlayerUse(player: PlayerEntity) =
    underlying.canPlayerUse(player)

  private fun checkSlot(localSlot: Int) =
    localSlot + minSlot < maxSlot

  override fun getStack(slot: Int): ItemStack =
    if (checkSlot(slot)) underlying.getStack(slot + minSlot) else EMPTY

  override fun removeStack(slot: Int, amount: Int): ItemStack =
    if (checkSlot(slot)) underlying.removeStack(slot + minSlot, amount) else EMPTY

  override fun removeStack(slot: Int): ItemStack =
    if (checkSlot(slot)) underlying.removeStack(slot + minSlot) else EMPTY

  override fun setStack(slot: Int, stack: ItemStack) {
    if (checkSlot(slot)) underlying.setStack(slot + minSlot, stack)
  }

  override fun markDirty() {
    underlying.markDirty()
  }

  override fun clear() {
    throw UnsupportedOperationException()
  }
}
