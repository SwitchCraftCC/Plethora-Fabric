package io.sc3.plethora.util

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.impl.transfer.DebugMessages
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl
import net.minecraft.item.ItemStack

// Based on InventorySlotWrapper
class DerivedInventorySlotWrapper(val storage: DerivedInventoryStorageImpl, val slot: Int) : SingleStackStorage() {
  private var lastReleasedSnapshot: ItemStack? = null

  override fun getStack(): ItemStack {
    return storage.inventory.getStack(slot)
  }

  override fun setStack(stack: ItemStack) {
    // Forced for equipment because this is used for rollbacks
    if (storage.inventory is EquipmentInventoryWrapper) {
      storage.inventory.forceSetStack(slot, stack)
    } else {
      storage.inventory.setStack(slot, stack)
    }
  }

  override fun insert(insertedVariant: ItemVariant, maxAmount: Long, transaction: TransactionContext?): Long {
    if (!canInsert(slot, (insertedVariant as ItemVariantImpl).cachedStack)) {
      return 0
    }
    val ret = super.insert(insertedVariant, maxAmount, transaction)

    return ret
  }

  private fun canInsert(slot: Int, stack: ItemStack): Boolean {
    return storage.inventory.isValid(slot, stack)
  }
  override fun extract(variant: ItemVariant?, maxAmount: Long, transaction: TransactionContext?): Long {
    return super.extract(variant, maxAmount, transaction)
  }

  override fun getCapacity(variant: ItemVariant): Int {
    return Math.min(storage.inventory.maxCountPerStack, variant.item.maxCount)
  }

  // We override updateSnapshots to also schedule a markDirty call for the backing inventory.
  override fun updateSnapshots(transaction: TransactionContext?) {
    storage.markDirtyParticipant.updateSnapshots(transaction)
    super.updateSnapshots(transaction)
  }

  override fun releaseSnapshot(snapshot: ItemStack) {
    lastReleasedSnapshot = snapshot
  }

  override fun onFinalCommit() {
    // Try to apply the change to the original stack
    val original = lastReleasedSnapshot!!
    val currentStack = stack
    if (!original.isEmpty && original.item === currentStack.item) {
      // None is empty and the items match: just update the amount and NBT, and reuse the original stack.
      original.count = currentStack.count
      original.nbt = if (currentStack.hasNbt()) currentStack.nbt!!.copy() else null
      stack = original
    } else {
      // Otherwise assume everything was taken from original so empty it.
      original.count = 0
    }
  }

  override fun toString(): String {
    return "InventorySlotWrapper[%s#%d]".formatted(DebugMessages.forInventory(storage.inventory), slot)
  }
}
