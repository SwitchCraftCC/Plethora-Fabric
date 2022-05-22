package pw.switchcraft.plethora.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class EquipmentInventoryWrapper implements Inventory {
    private static final EquipmentSlot[] VALUES = EquipmentSlot.values();
    private static final int SLOTS = VALUES.length;

    private final LivingEntity entity;

    public EquipmentInventoryWrapper(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public int size() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        validateSlotIndex(slot);
        return entity.getEquippedStack(VALUES[slot]);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (amount == 0) return ItemStack.EMPTY;

        validateSlotIndex(slot);
        ItemStack existing = getStack(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxCount());
        if (existing.getCount() <= toExtract) {
            setStack(slot, ItemStack.EMPTY);
            onContentsChanged(slot);
            return existing;
        } else {
            setStack(slot, copyStackWithSize(existing, existing.getCount() - toExtract));
            onContentsChanged(slot);
            return copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public ItemStack removeStack(int slot) {
        validateSlotIndex(slot);
        ItemStack existing = getStack(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;
        return removeStack(slot, existing.getCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        if (!isValid(slot, stack)) return;
        entity.equipStack(VALUES[slot], stack);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        validateSlotIndex(slot);

        EquipmentSlot slotType = VALUES[slot];

        // Verify the specified item stack is valid for the armor slot
        if (!stack.isEmpty() && slotType.getType() == EquipmentSlot.Type.ARMOR) {
            EquipmentSlot preferredSlot = LivingEntity.getPreferredEquipmentSlot(stack);
            return slotType == preferredSlot && entity.getEquippedStack(preferredSlot).isEmpty();
        }

        return true;
    }

    @Override
    public void markDirty() {
        if (entity instanceof PlayerEntity player) {
            player.getInventory().markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    private static void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= SLOTS) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0, " + SLOTS + "]");
        }
    }

    private void onContentsChanged(int slot) {
        if (entity instanceof MobEntity mob) {
            mob.setEquipmentDropChance(VALUES[slot], 1.1f);
        } else if (entity instanceof PlayerEntity player) {
            player.getInventory().markDirty();
        }
    }

    private static ItemStack copyStackWithSize(@Nonnull ItemStack itemStack, int size) {
        if (size == 0) return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }
}
