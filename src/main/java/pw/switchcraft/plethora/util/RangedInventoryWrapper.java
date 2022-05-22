package pw.switchcraft.plethora.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class RangedInventoryWrapper implements Inventory {
    private final Inventory underlying;
    private final int minSlot;
    private final int size;
    private final int maxSlot;

    public RangedInventoryWrapper(Inventory underlying, int minSlot, int size) {
        this.underlying = underlying;
        this.minSlot = minSlot;
        this.size = size;
        this.maxSlot = minSlot + size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (checkSlot(slot)) return underlying.getStack(slot + minSlot);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (checkSlot(slot)) return underlying.removeStack(slot + minSlot, amount);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (checkSlot(slot)) return underlying.removeStack(slot + minSlot);
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (checkSlot(slot)) underlying.setStack(slot + minSlot, stack);
    }

    @Override
    public void markDirty() {
        underlying.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return underlying.canPlayerUse(player);
    }

    @Override
    public int getMaxCountPerStack() {
        return underlying.getMaxCountPerStack();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    private boolean checkSlot(int localSlot) {
        return localSlot + minSlot < maxSlot;
    }
}
