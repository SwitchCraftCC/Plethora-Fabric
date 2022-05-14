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

public class NeuralInterfaceContainer extends ContainerComputerBase implements IContainerComputer {
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

    public NeuralInterfaceContainer(int syncId, PlayerInventory playerInventory, LivingEntity parent, ItemStack stack) {
        super(NEURAL_INTERFACE_HANDLER_TYPE, syncId, p -> true, NeuralComputerHandler.tryGetSidedComputer(parent, stack),
            ComputerFamily.ADVANCED);

        this.parent = parent;
        this.stack = stack;

        // TODO: NeuralItemHandler?
        inventory = new NeuralInterfaceInventory(INV_SIZE, stack);

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
