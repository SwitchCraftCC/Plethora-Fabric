package pw.switchcraft.plethora.gameplay.neural;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import pw.switchcraft.plethora.core.executor.TaskRunner;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static pw.switchcraft.plethora.gameplay.neural.ComputerItemHandler.*;
import static pw.switchcraft.plethora.gameplay.neural.NeuralHelpers.INV_SIZE;

public class NeuralComputer extends ServerComputer {
    private WeakReference<LivingEntity> entity;

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);
    private int moduleHash;

    private final Map<Identifier, NbtCompound> moduleData = new HashMap<>();
    private boolean moduleDataDirty = false;

    private final TaskRunner runner = new TaskRunner();

    public NeuralComputer(World world, int computerId, String label, int instanceId) {
        super(world, computerId, label, instanceId, ComputerFamily.ADVANCED, WIDTH, HEIGHT);
    }

    public TaskRunner getExecutor() {
        return runner;
    }

    public void readModuleData(NbtCompound nbt) {
        for (String key : nbt.getKeys()) {
            moduleData.put(new Identifier(key), nbt.getCompound(key));
        }
    }

    public NbtCompound getModuleData(Identifier id) {
        NbtCompound nbt = moduleData.get(id);
        if (nbt == null) moduleData.put(id, nbt = new NbtCompound());
        return nbt;
    }

    public void markModuleDataDirty() {
        moduleDataDirty = true;
    }

    public int getModuleHash() {
        return moduleHash;
    }

    /**
     * Update an sync peripherals
     *
     * @param owner The owner of the current peripherals
     */
    public boolean update(@Nonnull LivingEntity owner, @Nonnull ItemStack stack, int dirtyStatus) {
        // TODO: get handler

        LivingEntity existing = entity == null ? null : entity.get();
        if (existing != owner) {
            dirtyStatus = -1;
            entity = owner.isAlive() ? new WeakReference<>(owner) : null;
        }

        setLevel(owner.getEntityWorld());
        setPosition(owner.getBlockPos());

        // TODO: Sync changed slots
        // TODO: Update peripherals
        // TODO: Sync modules and peripherals

        runner.update();

        if (moduleDataDirty) {
            moduleDataDirty = false;

            NbtCompound nbt = new NbtCompound();
            for (Map.Entry<Identifier, NbtCompound> entry : moduleData.entrySet()){
                nbt.put(entry.getKey().toString(), entry.getValue());
            }
            stack.getOrCreateNbt().put(MODULE_DATA, nbt);
            return true;
        }

        return false;
    }
}
