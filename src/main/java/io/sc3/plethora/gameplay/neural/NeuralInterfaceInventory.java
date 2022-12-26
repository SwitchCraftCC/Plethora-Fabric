package pw.switchcraft.plethora.gameplay.neural;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import pw.switchcraft.plethora.mixin.SimpleInventoryAccessor;

import static pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler.DIRTY;
import static pw.switchcraft.plethora.gameplay.neural.NeuralHelpers.INV_SIZE;
import static pw.switchcraft.plethora.gameplay.neural.NeuralHelpers.isItemValid;

public class NeuralInterfaceInventory extends SimpleInventory {
    final ItemStack parent;

    public NeuralInterfaceInventory(ItemStack parent) {
        super(INV_SIZE);

        this.parent = parent;

        // Write the inventory to Items under the neural interface every time Inventory.markDirty is called
        addListener(i -> Inventories.writeNbt(parent.getOrCreateNbt(), getOwnStacks()));
    }

    @Override
    public void onOpen(PlayerEntity player) {
        super.onOpen(player);
        Inventories.readNbt(parent.getOrCreateNbt(), getOwnStacks());
    }

    @Override
    public void onClose(PlayerEntity player) {
        super.onClose(player);
        Inventories.writeNbt(parent.getOrCreateNbt(), getOwnStacks());
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return isItemValid(slot, stack);
    }

    public DefaultedList<ItemStack> getOwnStacks() {
        return ((SimpleInventoryAccessor) this).getStacks();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);

        NbtCompound nbt = parent.getOrCreateNbt();
        nbt.putShort(DIRTY, (short) (nbt.getShort(DIRTY) | (1 << slot)));
    }
}
