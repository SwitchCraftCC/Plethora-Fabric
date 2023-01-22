package io.sc3.plethora.util

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemStack.EMPTY

class EquipmentInventoryWrapper(private val entity: LivingEntity) : Inventory {
  override fun size() = SLOTS
  override fun isEmpty() = false
  override fun getMaxCountPerStack() = 64
  override fun canPlayerUse(player: PlayerEntity) = true

  override fun getStack(slot: Int): ItemStack {
    validateSlotIndex(slot)
    return entity.getEquippedStack(VALUES[slot])
  }

  override fun removeStack(slot: Int, amount: Int): ItemStack {
    if (amount == 0) return EMPTY

    validateSlotIndex(slot)
    val existing = getStack(slot)
    if (existing.isEmpty) return EMPTY

    val toExtract = amount.coerceAtMost(existing.maxCount)
    return if (existing.count <= toExtract) {
      setStack(slot, EMPTY)
      onContentsChanged(slot)
      existing
    } else {
      setStack(slot, copyStackWithSize(existing, existing.count - toExtract))
      onContentsChanged(slot)
      copyStackWithSize(existing, toExtract)
    }
  }

  override fun removeStack(slot: Int): ItemStack {
    validateSlotIndex(slot)
    val existing = getStack(slot)
    return if (existing.isEmpty) EMPTY else removeStack(slot, existing.count)
  }

  override fun setStack(slot: Int, stack: ItemStack) {
    validateSlotIndex(slot)
    if (!isValid(slot, stack)) return
    entity.equipStack(VALUES[slot], stack)
  }

  override fun isValid(slot: Int, stack: ItemStack): Boolean {
    validateSlotIndex(slot)
    val slotType = VALUES[slot]

    // Verify the specified item stack is valid for the armor slot
    if (!stack.isEmpty && slotType.type == EquipmentSlot.Type.ARMOR) {
      val preferredSlot = LivingEntity.getPreferredEquipmentSlot(stack)
      return slotType == preferredSlot && entity.getEquippedStack(preferredSlot).isEmpty
    }

    return true
  }

  override fun markDirty() {
    (entity as? PlayerEntity)?.inventory?.markDirty()
  }

  override fun clear() {
    throw UnsupportedOperationException()
  }

  private fun onContentsChanged(slot: Int) {
    (entity as? MobEntity)?.setEquipmentDropChance(VALUES[slot], 1.1f)
      ?: (entity as? PlayerEntity)?.inventory?.markDirty()
  }

  companion object {
    private val VALUES = EquipmentSlot.values()
    private val SLOTS = VALUES.size

    private fun validateSlotIndex(slot: Int) {
      require(slot in 0..SLOTS) { "Slot $slot not in valid range - [0, $SLOTS]" }
    }

    private fun copyStackWithSize(stack: ItemStack, size: Int): ItemStack {
      if (size == 0) return EMPTY
      val copy = stack.copy()
      copy.count = size
      return copy
    }
  }
}
