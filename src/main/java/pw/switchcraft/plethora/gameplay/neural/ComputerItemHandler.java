package pw.switchcraft.plethora.gameplay.neural;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ClientComputer;
import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.gameplay.BaseItem;
import pw.switchcraft.plethora.util.TinySlot;

import javax.annotation.Nonnull;

/**
 * Attempt to get computers from items
 */
public class ComputerItemHandler {
    public static final int WIDTH = 39;
    public static final int HEIGHT = 13;

    public static final String SESSION_ID = "session_id";
    public static final String INSTANCE_ID = "instance_id";
    public static final String COMPUTER_ID = "id";
    public static final String ITEMS = "items";
    public static final String DIRTY = "dirty";
    public static final String MODULE_DATA = "module_data";

    private ComputerItemHandler() {
    }

    public static NeuralComputer getServer(@Nonnull ItemStack stack, LivingEntity owner, TinySlot inventory) {
        NbtCompound nbt = BaseItem.getNbt(stack);

        final ServerComputerRegistry manager = ComputerCraft.serverComputerRegistry;
        final int sessionId = manager.getSessionID();

        NeuralComputer neural = null;
        if (nbt.getInt(SESSION_ID) == sessionId && nbt.contains(INSTANCE_ID) && manager.contains(nbt.getInt(INSTANCE_ID))) {
            ServerComputer computer = manager.get(nbt.getInt(INSTANCE_ID));

            if (computer instanceof NeuralComputer neuralComputer) {
                neural = neuralComputer;
            } else {
                Plethora.LOG.error("Computer is not NeuralComputer but " + computer);
            }
        }

        if (neural == null) {
            int instanceId = manager.getUnusedInstanceID();

            int computerId = nbt.contains(COMPUTER_ID)
                ? nbt.getInt(COMPUTER_ID)
                : ComputerCraftAPI.createUniqueNumberedSaveDir(owner.getEntityWorld(), "computer");

            String label = stack.hasCustomName() ? stack.getName().getString() : null;
            neural = new NeuralComputer(owner.getEntityWorld(), computerId, label, instanceId);
            neural.readModuleData(nbt.getCompound(MODULE_DATA));

            manager.add(instanceId, neural);

            nbt.putInt(SESSION_ID, sessionId);
            nbt.putInt(INSTANCE_ID, instanceId);
            nbt.putInt(COMPUTER_ID, computerId);

            neural.turnOn();
            if (inventory != null) inventory.markDirty();
        }

        return neural;
    }

    public static NeuralComputer tryGetServer(@Nonnull ItemStack stack) {
        NbtCompound nbt = BaseItem.getNbt(stack);

        final ServerComputerRegistry manager = ComputerCraft.serverComputerRegistry;
        final int sessionId = manager.getSessionID();

        if (nbt.getInt(SESSION_ID) == sessionId && nbt.contains(INSTANCE_ID) && manager.contains(nbt.getInt(INSTANCE_ID))) {
            ServerComputer computer = manager.get(nbt.getInt(INSTANCE_ID));
            if (computer instanceof NeuralComputer neuralComputer) {
                return neuralComputer;
            } else {
                Plethora.LOG.error("Computer is not NeuralComputer but " + computer);
                return null;
            }
        } else {
            return null;
        }
    }

    public static ClientComputer getClient(@Nonnull ItemStack stack) {
        NbtCompound nbt = BaseItem.getNbt(stack);

        int instanceId = nbt.getInt(INSTANCE_ID);
        if (instanceId < 0) return null;

        if (!ComputerCraft.clientComputerRegistry.contains(instanceId)) {
            ComputerCraft.clientComputerRegistry.add(instanceId, new ClientComputer(instanceId));
        }

        return ComputerCraft.clientComputerRegistry.get(instanceId);
    }

    public static IComputer tryGetSidedComputer(@Nonnull LivingEntity entity, @Nonnull ItemStack stack) {
        if (entity.getEntityWorld().isClient) return getClient(stack);
        else return tryGetServer(stack);
    }
}
