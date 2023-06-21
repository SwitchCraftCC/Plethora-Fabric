package io.sc3.plethora.util

import io.sc3.plethora.api.reference.IReference
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemStack.EMPTY
import net.minecraft.item.ItemStack.canCombine

class EquipmentInventoryWrapper(
  private val entityReference: IReference<LivingEntity>
) : Inventory {
  private val entity: LivingEntity
    get() = entityReference.get()

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
      setStack(slot, existing.copyWithCount(existing.count - toExtract))
      onContentsChanged(slot)
      existing.copyWithCount(toExtract)
    }
  }

  override fun removeStack(slot: Int): ItemStack {
    validateSlotIndex(slot)
    val existing = getStack(slot)
    return if (existing.isEmpty) EMPTY else removeStack(slot, existing.count)
  }

  override fun setStack(slot: Int, stack: ItemStack) {
    validateSlotIndex(slot)
    entity.equipStack(VALUES[slot], stack)
  }

  override fun isValid(slot: Int, stack: ItemStack): Boolean {
    validateSlotIndex(slot)
    val slotType = VALUES[slot]

    // Verify the specified item stack is valid for the armor slot
    if (!stack.isEmpty && slotType.type == EquipmentSlot.Type.ARMOR) {
      val preferredSlot = LivingEntity.getPreferredEquipmentSlot(stack)
      return slotType == preferredSlot
        && (entity.getEquippedStack(preferredSlot).isEmpty || canCombine(entity.getEquippedStack(slotType), stack))
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
  }
}
