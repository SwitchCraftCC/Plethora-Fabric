package pw.switchcraft.plethora.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TinySlot {
    private final ItemStack stack;

    public TinySlot(@Nonnull ItemStack stack) {
        Objects.requireNonNull(stack, "stack cannot be null");
        this.stack = stack;
    }

    @Nonnull
    public ItemStack getStack() {
        return stack;
    }

    public void markDirty() {

    }

    public static class InventorySlot extends TinySlot {
        private final Inventory inventory;

        public InventorySlot(@Nonnull ItemStack stack, @Nonnull Inventory inventory) {
            super(stack);
            this.inventory = inventory;
        }

        @Override
        public void markDirty() {
            inventory.markDirty();
        }
    }
}
