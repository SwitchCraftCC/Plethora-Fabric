package io.sc3.plethora.util

import io.sc3.plethora.api.reference.IReference
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemStack.EMPTY

class RangedInventoryWrapper<T : LivingEntity>(
  entityReference: IReference<T>,
  private val minSlot: Int,
  private val size: Int,
  private val inventoryProvider: (T) -> Inventory?
) : LivingEntityInventoryWrapper<T>(entityReference), Inventory {
  private val maxSlot = minSlot + size

  override fun size() = size
  override fun isEmpty() = false
  override fun getMaxCountPerStack() = safeInv?.maxCountPerStack ?: 64

  override fun canPlayerUse(player: PlayerEntity) =
    safeInv?.canPlayerUse(player) ?: false

  private fun checkSlot(localSlot: Int) =
    localSlot + minSlot < maxSlot

  override fun getStack(slot: Int): ItemStack =
    if (checkSlot(slot)) inv.getStack(slot + minSlot) else EMPTY

  override fun removeStack(slot: Int, amount: Int): ItemStack =
    if (checkSlot(slot)) inv.removeStack(slot + minSlot, amount) else EMPTY

  override fun removeStack(slot: Int): ItemStack =
    if (checkSlot(slot)) inv.removeStack(slot + minSlot) else EMPTY

  override fun setStack(slot: Int, stack: ItemStack) {
    if (checkSlot(slot)) inv.setStack(slot + minSlot, stack)
  }

  override fun markDirty() {
    inv.markDirty()
  }

  override fun clear() {
    throw UnsupportedOperationException()
  }

  override fun provideInventory(entity: T): Inventory? =
    inventoryProvider(entity)
}
