package io.sc3.plethora.gameplay.neural;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.impl.PocketUpgrades;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import io.sc3.plethora.api.EntityWorldLocation;
import io.sc3.plethora.api.IWorldLocation;
import io.sc3.plethora.api.method.ContextKeys;
import io.sc3.plethora.api.method.CostHelpers;
import io.sc3.plethora.api.method.ICostHandler;
import io.sc3.plethora.api.module.BasicModuleContainer;
import io.sc3.plethora.api.module.IModuleAccess;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.api.module.IModuleHandler;
import io.sc3.plethora.api.reference.ConstantReference;
import io.sc3.plethora.api.reference.IReference;
import io.sc3.plethora.core.*;
import io.sc3.plethora.gameplay.modules.ModuleItem;
import io.sc3.plethora.gameplay.modules.ModulePeripheral;
import io.sc3.plethora.gameplay.registry.Registration;

import javax.annotation.Nonnull;
import java.util.*;

import static io.sc3.plethora.Plethora.config;
import static io.sc3.plethora.api.reference.Reference.entity;

public class NeuralHelpers {
    public static final int MODULE_SIZE = 5;
    public static final int PERIPHERAL_SIZE = 5;

    public static final int INV_SIZE = MODULE_SIZE + PERIPHERAL_SIZE;

    public static final int BACK = 2;

    public static Optional<Pair<SlotReference, ItemStack>> getSlot(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity)
            .flatMap(c -> c.getEquipped(Registration.ModItems.NEURAL_INTERFACE)
                .stream().findFirst());
    }

    public static Optional<ItemStack> getStack(LivingEntity entity) {
        var slotPair = getSlot(entity);
        return slotPair.map(Pair::getRight).filter(stack -> !stack.isEmpty());
    }

    public static boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (slot < PERIPHERAL_SIZE) {
            // Check if the item stack is a peripheral by checking if it is included in the allowed peripherals list and
            // is registered as a pocket computer upgrade. This may be made more extensible in the future.
            Identifier id = Registries.ITEM.getId(stack.getItem());
            IPocketUpgrade upgrade = PocketUpgrades.instance().get(stack);
            return upgrade != null && config.neuralInterface.peripheralItemIds.contains(id.toString());
        } else {
            return stack.getItem() instanceof ModuleItem;
        }
    }

    public static IPeripheral buildPeripheral(@Nonnull NeuralPocketAccess access, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) return null;

        IPocketUpgrade upgrade = PocketUpgrades.instance().get(stack);
        if (upgrade == null) return null;

        return upgrade.createPeripheral(access);
    }

    public static IDynamicPeripheral buildModules(final NeuralComputer computer,
                                                  final DefaultedList<ItemStack> inventory, Entity owner) {
        final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(MODULE_SIZE, ItemStack.EMPTY);
        Set<Identifier> modules = new HashSet<>();
        Set<Pair<IModuleHandler, ItemStack>> moduleHandlers = new HashSet<>();
        final int moduleHash = computer.getModuleHash();

        for (int i = 0; i < MODULE_SIZE; i++) {
            ItemStack stack = inventory.get(PERIPHERAL_SIZE + i);
            if (stack.isEmpty()) continue;

            stacks.set(i, stack = stack.copy());

            if (!(stack.getItem() instanceof IModuleHandler moduleHandler)) continue;

            Identifier module = moduleHandler.getModule();
            // TODO: Check module blacklist

            modules.add(module);
            moduleHandlers.add(new Pair<>(moduleHandler, stack));
        }

        if (modules.isEmpty()) return null;

        final IModuleContainer container = new BasicModuleContainer(modules);
        Map<Identifier, NeuralAccess> accessMap = new HashMap<>();

        ICostHandler cost = CostHelpers.getCostHandler(owner);
        IReference<IModuleContainer> containerRef = new ConstantReference<IModuleContainer>() {
            @Nonnull
            @Override
            public IModuleContainer get() throws LuaException {
                for (int i = 0; i < MODULE_SIZE; i++) {
                    ItemStack oldStack = stacks.get(i);
                    ItemStack newStack = inventory.get(PERIPHERAL_SIZE + i);
                    if (!oldStack.isEmpty() && !ItemStack.areItemsEqual(oldStack, newStack)) {
                        IModuleHandler moduleHandler = (IModuleHandler) oldStack.getItem();
                        throw new LuaException("The " + moduleHandler.getModule() + " module has been removed");
                    }
                }
                return container;
            }

            @Nonnull
            @Override
            public IModuleContainer safeGet() throws LuaException {
                if (moduleHash != computer.getModuleHash()) {
                    throw new LuaException("A moudle has changed");
                }

                return container;
            }
        };

        ContextFactory<IModuleContainer> builder = ContextFactory.of(container, containerRef)
            .withCostHandler(cost)
            .withModules(container, containerRef)
            .addContext(ContextKeys.ORIGIN, new EntityWorldLocation(owner))
            .addContext(ContextKeys.ORIGIN, owner, entity(owner));

        for (Pair<IModuleHandler, ItemStack> handler : moduleHandlers) {
            Identifier module = handler.getLeft().getModule();
            NeuralAccess access = accessMap.get(module);
            if (access == null) {
                accessMap.put(module, access = new NeuralAccess(owner, computer, handler.getLeft(), container));
            }

            handler.getLeft().getAdditionalContext(handler.getRight(), access, builder);
        }

        Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> paired
            = MethodRegistry.instance.getMethodsPaired(builder.getBaked());
        if (paired.getLeft().isEmpty()) return null;

        ModulePeripheral peripheral = new ModulePeripheral("neuralInterface", owner, paired, computer.getExecutor(), builder.getAttachments(), moduleHash);
        for (NeuralAccess access : accessMap.values()) {
            access.wrapper = peripheral;
        }
        return peripheral;
    }

    private static final class NeuralAccess implements IModuleAccess {
        private AttachableWrapperPeripheral wrapper;

        private final Entity owner;
        private final NeuralComputer computer;
        private final Identifier module;
        private final IModuleContainer container;
        private final IWorldLocation location;

        private NeuralAccess(Entity owner, NeuralComputer computer, IModuleHandler module, IModuleContainer container) {
            this.owner = owner;
            this.computer = computer;
            this.module = module.getModule();
            this.container = container;
            location = new EntityWorldLocation(owner);
        }

        @Nonnull
        @Override
        public Object getOwner() {
            return owner;
        }

        @Nonnull
        @Override
        public IWorldLocation getLocation() {
            return location;
        }

        @Nonnull
        @Override
        public IModuleContainer getContainer() {
            return container;
        }

        @Nonnull
        @Override
        public NbtCompound getData() {
            return computer.getModuleData(module);
        }

        @Nonnull
        @Override
        public MinecraftServer getServer() {
            return Objects.requireNonNull(owner.getServer());
        }

        @Override
        public void markDataDirty() {
            computer.markModuleDataDirty();
        }

        @Override
        public void queueEvent(@Nonnull String event, @Nullable Object... args) {
            if (wrapper != null) wrapper.queueEvent(event, args);
        }
    }
}
