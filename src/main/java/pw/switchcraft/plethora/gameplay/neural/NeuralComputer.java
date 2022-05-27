package pw.switchcraft.plethora.gameplay.neural;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.PocketUpgrades;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import pw.switchcraft.plethora.core.executor.TaskRunner;
import pw.switchcraft.plethora.util.Helpers;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler.*;
import static pw.switchcraft.plethora.gameplay.neural.NeuralHelpers.*;

public class NeuralComputer extends ServerComputer {
    private WeakReference<LivingEntity> entity;

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);
    private int moduleHash;

    private final Map<Identifier, NbtCompound> moduleData = new HashMap<>();
    private boolean moduleDataDirty = false;

    private final TaskRunner runner = new TaskRunner();
    private final NeuralPocketAccess access;

    public NeuralComputer(World world, int computerId, String label, int instanceId) {
        super(world, computerId, label, instanceId, ComputerFamily.ADVANCED, WIDTH, HEIGHT);
        access = new NeuralPocketAccess(this);
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
    public boolean update(@Nonnull LivingEntity owner, @Nonnull ItemStack neuralStack, int dirtyStatus) {
        LivingEntity existing = entity == null ? null : entity.get();
        if (existing != owner) {
            dirtyStatus = -1;
            entity = owner.isAlive() ? new WeakReference<>(owner) : null;
        }

        setLevel(owner.getEntityWorld());
        setPosition(owner.getBlockPos());

        // Sync changed slots
        if (dirtyStatus != 0) {
            stacks.clear();
            Inventories.readNbt(neuralStack.getOrCreateNbt(), stacks);
            moduleHash = Helpers.hashStacks(stacks.subList(PERIPHERAL_SIZE, INV_SIZE));
        }

        // Update peripherals
        for (int slot = 0; slot < PERIPHERAL_SIZE; slot++) {
            ItemStack stack = stacks.get(slot);
            if (stack.isEmpty()) continue;

            IPocketUpgrade upgrade = PocketUpgrades.get(stack);
            if (upgrade == null) continue;

            ComputerSide side = ComputerSide.valueOf(slot < BACK ? slot : slot + 1);
            IPeripheral peripheral = getPeripheral(side);
            if (peripheral == null) continue;

            upgrade.update(access, peripheral);
        }

        if (dirtyStatus != 0) {
            for (int slot = 0; slot < PERIPHERAL_SIZE; slot++) {
                if ((dirtyStatus & (1 << slot)) == 1 << slot) {
                    // We skip the "back" slot
                    setPeripheral(ComputerSide.valueOf(slot < BACK ? slot : slot + 1),
                        buildPeripheral(access, stacks.get(slot)));
                }
            }

            // If the modules have changed.
            if (dirtyStatus >> PERIPHERAL_SIZE != 0) {
                setPeripheral(ComputerSide.BACK, buildModules(this, stacks, owner));
            }
        }

        runner.update();

        if (moduleDataDirty) {
            moduleDataDirty = false;

            NbtCompound nbt = new NbtCompound();
            for (Map.Entry<Identifier, NbtCompound> entry : moduleData.entrySet()){
                nbt.put(entry.getKey().toString(), entry.getValue());
            }
            neuralStack.getOrCreateNbt().put(MODULE_DATA, nbt);
            return true;
        }

        return false;
    }

    WeakReference<LivingEntity> getEntity() {
        return entity;
    }
}
