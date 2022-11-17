package pw.switchcraft.plethora.gameplay.registry;

import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.api.PlethoraEvents;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.api.module.IModuleRegistry;
import pw.switchcraft.plethora.core.PocketUpgradeModule;
import pw.switchcraft.plethora.core.TurtleUpgradeModule;
import pw.switchcraft.plethora.gameplay.BaseBlockEntity;
import pw.switchcraft.plethora.gameplay.data.recipes.handlers.RecipeHandlers;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlockEntity;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorPeripheral;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorType;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesModuleItem;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler;
import pw.switchcraft.plethora.gameplay.modules.introspection.IntrospectionModuleItem;
import pw.switchcraft.plethora.gameplay.modules.keyboard.KeyboardModuleItem;
import pw.switchcraft.plethora.gameplay.modules.kinetic.KineticModuleItem;
import pw.switchcraft.plethora.gameplay.modules.kinetic.KineticTurtleUpgrade;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserEntity;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserModuleItem;
import pw.switchcraft.plethora.gameplay.modules.scanner.ScannerModuleItem;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorModuleItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralConnectorItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenFactory;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler;
import pw.switchcraft.plethora.gameplay.redstone.RedstoneIntegratorBlock;
import pw.switchcraft.plethora.gameplay.redstone.RedstoneIntegratorBlockEntity;
import pw.switchcraft.plethora.gameplay.redstone.RedstoneIntegratorTicker;
import pw.switchcraft.plethora.integration.computercraft.registry.ComputerCraftMetaRegistration;
import pw.switchcraft.plethora.integration.computercraft.registry.ComputerCraftMethodRegistration;
import pw.switchcraft.plethora.integration.vanilla.registry.VanillaConverterRegistration;
import pw.switchcraft.plethora.integration.vanilla.registry.VanillaMetaRegistration;
import pw.switchcraft.plethora.integration.vanilla.registry.VanillaMethodRegistration;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.util.registry.Registry.*;
import static pw.switchcraft.plethora.Plethora.log;

public final class Registration {
    public static final EntityType<LaserEntity> LASER_ENTITY = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(Plethora.modId, "laser"),
        EntityType.Builder.<LaserEntity>create(LaserEntity::new, SpawnGroup.MISC)
            .setDimensions(0.25F, 0.25F)
            .maxTrackingRange(4).trackingTickInterval(10)
            .build("laser")
    );

    public static void init() {
        // Similar to how CC behaves - touch each static class to force the static initializers to run.
        Object[] o = {
            ModBlockEntities.MANIPULATOR_MARK_1,
            ModBlocks.MANIPULATOR_MARK_1,
            ModItems.NEURAL_CONNECTOR,
            ModScreens.NEURAL_INTERFACE_HANDLER_TYPE,
            ModTurtleUpgradeSerialisers.MODULE,
          ModPocketUpgradeSerialisers.MODULE,
        };
        log.trace("oh no:" + (o[0] != null ? "yes" : "NullPointerException")); // lig was here

        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Plethora.modId, "neural_interface"),
            ModScreens.NEURAL_INTERFACE_HANDLER_TYPE);

        PlethoraEvents.REGISTER.register(api -> {
            // Vanilla registration
            VanillaConverterRegistration.registerConverters(api.converterRegistry());
            VanillaMetaRegistration.registerMetaProviders(api.metaRegistry());
            VanillaMethodRegistration.registerMethods(api.methodRegistry());

            // Plethora registration
            PlethoraMetaRegistration.registerMetaProviders(api.metaRegistry());
            PlethoraMethodRegistration.registerMethods(api.methodRegistry());

            // ComputerCraft integration registration
            ComputerCraftMetaRegistration.registerMetaProviders(api.metaRegistry());
            ComputerCraftMethodRegistration.registerMethods(api.methodRegistry());

            VanillaDetailRegistries.ITEM_STACK.addProvider(ItemDetailsProvider.INSTANCE);

            // Manipulator peripheral
            PeripheralLookup.get().registerForBlockEntity(ManipulatorPeripheral::getPeripheral, ModBlockEntities.MANIPULATOR_MARK_1);
            PeripheralLookup.get().registerForBlockEntity(ManipulatorPeripheral::getPeripheral, ModBlockEntities.MANIPULATOR_MARK_2);
            PeripheralLookup.get().registerForBlockEntity(RedstoneIntegratorBlockEntity::getPeripheral, ModBlockEntities.REDSTONE_INTEGRATOR);
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
            if (blockEntity instanceof BaseBlockEntity base) base.onChunkLoaded();
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
            if (blockEntity instanceof BaseBlockEntity base) base.onChunkUnloaded();
        });

        RedstoneIntegratorTicker.registerEvents();
        CanvasHandler.registerServerEvents();

        RecipeHandlers.registerSerializers();
    }

    public static final class ModItems {
        private static final ItemGroup itemGroup = FabricItemGroupBuilder.build(
            new Identifier(Plethora.modId, "main"),
            () -> new ItemStack(ModItems.NEURAL_CONNECTOR) // TODO: Use the neural interface here
        );

        public static final NeuralConnectorItem NEURAL_CONNECTOR =
            register("neural_connector", new NeuralConnectorItem(properties().maxCount(1)));
        public static final NeuralInterfaceItem NEURAL_INTERFACE =
            register("neural_interface", new NeuralInterfaceItem(properties().maxCount(1)));

        public static final GlassesModuleItem GLASSES_MODULE = registerModule("glasses", GlassesModuleItem::new);
        public static final IntrospectionModuleItem INTROSPECTION_MODULE = registerModule("introspection", IntrospectionModuleItem::new);
        public static final KeyboardModuleItem KEYBOARD_MODULE = registerModule("keyboard", KeyboardModuleItem::new);
        public static final KineticModuleItem KINETIC_MODULE = registerModule("kinetic", KineticModuleItem::new);
        public static final LaserModuleItem LASER_MODULE = registerModule("laser", LaserModuleItem::new);
        public static final ScannerModuleItem SCANNER_MODULE = registerModule("scanner", ScannerModuleItem::new);
        public static final SensorModuleItem SENSOR_MODULE = registerModule("sensor", SensorModuleItem::new);

        public static final BlockItem MANIPULATOR_MARK_1 = ofBlock(ModBlocks.MANIPULATOR_MARK_1, BlockItem::new);
        public static final BlockItem MANIPULATOR_MARK_2 = ofBlock(ModBlocks.MANIPULATOR_MARK_2, BlockItem::new);
        public static final BlockItem REDSTONE_INTEGRATOR = ofBlock(ModBlocks.REDSTONE_INTEGRATOR, BlockItem::new);

        private static Item.Settings properties() {
            return new Item.Settings().group(itemGroup);
        }

        private static <B extends Block, I extends Item> I ofBlock(B parent, BiFunction<B, Item.Settings, I> supplier) {
            return Registry.register(ITEM, BLOCK.getId(parent), supplier.apply(parent, properties()));
        }

        private static <T extends Item> T register(String id, T item) {
            return Registry.register(ITEM, new Identifier(Plethora.modId, id), item);
        }

        private static <T extends Item> T registerModule(String id, Function<Item.Settings, T> itemCtor) {
            return register("module_" + id, itemCtor.apply(properties().maxCount(1)));
        }
    }

    public static class ModBlocks {
        public static final Block MANIPULATOR_MARK_1 = register("manipulator_mark_1",
            new ManipulatorBlock(properties(), ManipulatorType.MARK_1));
        public static final Block MANIPULATOR_MARK_2 = register("manipulator_mark_2",
            new ManipulatorBlock(properties(), ManipulatorType.MARK_2));
        public static final Block REDSTONE_INTEGRATOR = register("redstone_integrator",
            new RedstoneIntegratorBlock(properties()));

        private static <T extends Block> T register(String id, T value) {
            return Registry.register(BLOCK, new Identifier(Plethora.modId, id), value);
        }

        private static Block.Settings properties() {
            return Block.Settings.of(Material.STONE).strength(2.0F).nonOpaque();
        }
    }

    public static final class ModBlockEntities {
        public static final BlockEntityType<ManipulatorBlockEntity> MANIPULATOR_MARK_1 = ofBlock(
            ModBlocks.MANIPULATOR_MARK_1, "manipulator_mark_1", (blockPos, blockState) ->
                new ManipulatorBlockEntity(ModBlockEntities.MANIPULATOR_MARK_1, blockPos, blockState,
                    ManipulatorType.MARK_1));
        public static final BlockEntityType<ManipulatorBlockEntity> MANIPULATOR_MARK_2 = ofBlock(
            ModBlocks.MANIPULATOR_MARK_2, "manipulator_mark_2", (blockPos, blockState) ->
                new ManipulatorBlockEntity(ModBlockEntities.MANIPULATOR_MARK_2, blockPos, blockState,
                    ManipulatorType.MARK_2));
        public static final BlockEntityType<RedstoneIntegratorBlockEntity> REDSTONE_INTEGRATOR = ofBlock(
            ModBlocks.REDSTONE_INTEGRATOR, "redstone_integrator", (blockPos, blockState) ->
                new RedstoneIntegratorBlockEntity(ModBlockEntities.REDSTONE_INTEGRATOR, blockPos, blockState));

        private static <T extends BlockEntity> BlockEntityType<T> ofBlock(Block block, String id,
                                                                          BiFunction<BlockPos, BlockState, T> factory) {
            BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create(factory::apply, block).build();
            return Registry.register(BLOCK_ENTITY_TYPE, new Identifier(Plethora.modId, id), blockEntityType);
        }
    }

    public static final class ModScreens {
        public static final ExtendedScreenHandlerType<NeuralInterfaceScreenHandler> NEURAL_INTERFACE_HANDLER_TYPE =
            new ExtendedScreenHandlerType<>(NeuralInterfaceScreenFactory::fromPacket);
    }

    public static final class ModTurtleUpgradeSerialisers {
      private static <T extends TurtleUpgradeSerialiser<?>> T register(Identifier name, T serialiser) {
        @SuppressWarnings("unchecked")
        var registry = (Registry<? super TurtleUpgradeSerialiser<?>>) REGISTRIES.get(TurtleUpgradeSerialiser.REGISTRY_ID.getValue());
        if (registry == null) throw new IllegalStateException("ComputerCraft has not initialised yet?");
        Registry.register(registry, name, serialiser);
        return serialiser;
      }

      public static final TurtleUpgradeSerialiser<TurtleUpgradeModule> MODULE = register(
        new Identifier(Plethora.modId, "module"),
        TurtleUpgradeSerialiser.simpleWithCustomItem((id, item) ->
          new TurtleUpgradeModule(item, (IModuleHandler) item.getItem(), item.getTranslationKey() + ".adjective"))
      );

      public static final TurtleUpgradeSerialiser<KineticTurtleUpgrade> KINETIC_AUGMENT = register(
        ModItems.KINETIC_MODULE.getModule(),
        TurtleUpgradeSerialiser.simpleWithCustomItem((id, item) ->
          new KineticTurtleUpgrade(item, ModItems.KINETIC_MODULE, item.getTranslationKey() + ".adjective"))
      );
    }

  public static final class ModPocketUpgradeSerialisers {
    private static <T extends PocketUpgradeSerialiser<?>> T register(Identifier name, T serialiser) {
      @SuppressWarnings("unchecked")
      var registry = (Registry<? super PocketUpgradeSerialiser<?>>) REGISTRIES.get(PocketUpgradeSerialiser.REGISTRY_ID.getValue());
      if (registry == null) throw new IllegalStateException("ComputerCraft has not initialised yet?");
      Registry.register(registry, name, serialiser);
      return serialiser;
    }

    public static final PocketUpgradeSerialiser<PocketUpgradeModule> MODULE = register(
      new Identifier(Plethora.modId, "module"),
      PocketUpgradeSerialiser.simpleWithCustomItem((id, item) ->
        new PocketUpgradeModule(item, (IModuleHandler) item.getItem(), item.getTranslationKey() + ".adjective"))
    );
  }
}
