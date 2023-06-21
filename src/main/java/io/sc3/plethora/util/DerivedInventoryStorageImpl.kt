package io.sc3.plethora.util

import com.google.common.collect.MapMaker
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.fabricmc.fabric.impl.transfer.DebugMessages
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.Direction
import java.util.*

// Based on InventoryStorageImpl
class DerivedInventoryStorageImpl(val inventory: Inventory) :
  CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>>(emptyList<SingleSlotStorage<ItemVariant>>()), InventoryStorage {

  /**
   * This `backingList` is the real list of wrappers.
   * The `parts` in the superclass is the public-facing unmodifiable sublist with exactly the right amount of slots.
   */
  val backingList: MutableList<DerivedInventorySlotWrapper> = mutableListOf()

  /**
   * This participant ensures that markDirty is only called once for the entire inventory.
   */
  val markDirtyParticipant: MarkDirtyParticipant = MarkDirtyParticipant()
  companion object {
    private val WRAPPERS: MutableMap<Inventory, DerivedInventoryStorageImpl> = MapMaker().weakValues().makeMap()
    fun of(inventory: Inventory): InventoryStorage {
      val storage: DerivedInventoryStorageImpl = WRAPPERS.computeIfAbsent(inventory) { inv: Inventory ->
        return@computeIfAbsent DerivedInventoryStorageImpl(inv)
      }
      storage.resizeSlotList();
		  return storage.getSidedWrapper(null);
    }
  }

  override fun getSlots(): MutableList<SingleSlotStorage<ItemVariant>>? {
    return parts
  }

  /**
   * Resize slot list to match the current size of the inventory.
   */
  private fun resizeSlotList() {
    val inventorySize = inventory.size()

    // If the public-facing list must change...
    if (inventorySize != parts.size) {
      // Ensure we have enough wrappers in the backing list.
      while (backingList.size < inventorySize) {
        backingList.add(DerivedInventorySlotWrapper(this, backingList.size))
      }

      // Update the public-facing list.
      parts = Collections.unmodifiableList<SingleSlotStorage<ItemVariant>>(backingList.subList(0, inventorySize))
    }
  }

  private fun getSidedWrapper(direction: Direction?): InventoryStorage {
    return this
  }

  override fun toString(): String {
    return "InventoryStorage[" + DebugMessages.forInventory(inventory) + "]"
  }
  inner class MarkDirtyParticipant : SnapshotParticipant<Boolean>() {
    override fun createSnapshot(): Boolean {
      return java.lang.Boolean.TRUE
    }

    override fun readSnapshot(snapshot: Boolean) {}
    override fun onFinalCommit() {
      inventory.markDirty()
    }
  }

}
