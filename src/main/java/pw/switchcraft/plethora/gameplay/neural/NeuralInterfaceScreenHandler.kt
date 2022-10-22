package pw.switchcraft.plethora.gameplay.neural;

import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.core.IContainerComputer;
import dan200.computercraft.shared.computer.inventory.ContainerComputerBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import pw.switchcraft.plethora.util.Vec2i;

import static pw.switchcraft.plethora.gameplay.client.gui.GuiNeuralInterface.BORDER;
import static pw.switchcraft.plethora.gameplay.neural.NeuralHelpers.*;
import static pw.switchcraft.plethora.gameplay.registry.Registration.ModScreens.NEURAL_INTERFACE_HANDLER_TYPE;

public class NeuralInterfaceScreenHandler extends ContainerComputerBase implements IContainerComputer {
    public static final int START_Y = 134;

    private static final int MAIN_START_X = BORDER + ComputerSidebar.WIDTH;
    public static final int NEURAL_START_X = 185 + ComputerSidebar.WIDTH;

    // Slot size
    public static final int S = 18;

    // Pixel coordinates for the slots
    public static final Vec2i[] POSITIONS = new Vec2i[]{
        new Vec2i(NEURAL_START_X + 1 + S, START_Y + 1 + 2 * S),
        new Vec2i(NEURAL_START_X + 1 + S, START_Y + 1),

        // Center
        new Vec2i(NEURAL_START_X + 1 + S, START_Y + 1 + S),

        new Vec2i(NEURAL_START_X + 1 + 2 * S, START_Y + 1 + S),
        new Vec2i(NEURAL_START_X + 1, START_Y + 1 + S)
    };

    public static final Vec2i SWAP = new Vec2i(NEURAL_START_X + 1 + 2 * S, START_Y + 1 + 2 * S);

    private final LivingEntity parent;
    private final ItemStack stack;

    public final Slot[] peripheralSlots;
    public final Slot[] moduleSlots;

    private final Inventory inventory;

    public NeuralInterfaceScreenHandler(int syncId, PlayerInventory playerInventory, LivingEntity parent, ItemStack stack) {
        super(NEURAL_INTERFACE_HANDLER_TYPE, syncId, p -> true, NeuralComputerHandler.tryGetSidedComputer(parent, stack),
            ComputerFamily.ADVANCED);

        this.parent = parent;
        this.stack = stack;

        // TODO: NeuralItemHandler?
        inventory = new NeuralInterfaceInventory(stack);

        peripheralSlots = addSlots(inventory, 0, PERIPHERAL_SIZE);
        moduleSlots = addSlots(inventory, PERIPHERAL_SIZE, MODULE_SIZE);

        // TODO: is there a Vanilla/Fabric API method for this?
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, MAIN_START_X + x * S, START_Y + 1 + y * S));
            }
        }

        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, MAIN_START_X + x * S, START_Y + 54 + 5));
        }

        inventory.onOpen(playerInventory.player);
    }

    private Slot[] addSlots(Inventory inv, int offset, int length) {
        Slot[] slots = new Slot[length];
        for (int i = 0; i < length; i++) {
            addSlot(slots[i] = new NeuralSlot(inv, offset + i, 0, 0));
        }
        return slots;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player != null && player.isAlive() && parent.isAlive()
            && NeuralHelpers.getStack(parent).map(s -> s.equals(stack)).orElse(false);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        // Ensure the inventory is saved
        inventory.onClose(player);
    }

    @Nullable
    @Override
    public IComputer getComputer() {
        return NeuralComputerHandler.tryGetSidedComputer(parent, stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        // This function is solely responsible for shift+click 'quick move' behaviour. The original ScreenHandler
        // implementation of transferSlot() causes an infinite loop as it returns the same item stack when moving it,
        // which never shows up in practice because all implementations of ScreenHandler.transferSlot() are overridden.
        // See: https://github.com/FabricMC/yarn/issues/2944

        // Intended implementation:
        // - If any items were moved out of the slot, it returns the original item stack in the slot.
        // - If no items could be moved, it returns ItemStack.EMPTY.

        Slot slot = slots.get(index);
        if (slot == null || !slot.hasStack()) return ItemStack.EMPTY;

        ItemStack existing = slot.getStack().copy();
        ItemStack result = existing.copy();
        if (index < INV_SIZE) {
            // One of our neural slots, insert into the player's inventory
            if (!insertItem(existing, INV_SIZE, INV_SIZE + 36, true)) return ItemStack.EMPTY;
        } else {
            // One of the player's inventory slots (hopefully!), insert into the neural inventory
            if (!insertItem(existing, 0, INV_SIZE, false)) return ItemStack.EMPTY;
        }

        if (existing.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        if (existing.getCount() == result.getCount()) return ItemStack.EMPTY;

        slot.onTakeItem(player, existing);
        return result;
    }

    public static final class NeuralSlot extends Slot {
        private final int index;

        public NeuralSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.index = index;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !stack.isEmpty() && inventory.isValid(index, stack);
        }
    }
}
