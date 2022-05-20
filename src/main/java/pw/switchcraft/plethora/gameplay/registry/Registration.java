package pw.switchcraft.plethora.gameplay.registry;

import dan200.computercraft.api.ComputerCraftAPI;
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
import pw.switchcraft.plethora.api.module.IModuleRegistry;
import pw.switchcraft.plethora.gameplay.BaseBlockEntity;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlockEntity;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorPeripheral;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorType;
import pw.switchcraft.plethora.gameplay.modules.keyboard.KeyboardModuleItem;
import pw.switchcraft.plethora.gameplay.modules.kinetic.KineticModuleItem;
import pw.switchcraft.plethora.gameplay.modules.kinetic.KineticRecipe;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserEntity;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserModuleItem;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserRecipe;
import pw.switchcraft.plethora.gameplay.modules.scanner.ScannerModuleItem;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorModuleItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralConnectorItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenFactory;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler;
import pw.switchcraft.plethora.integration.vanilla.registry.VanillaConverterRegistration;
import pw.switchcraft.plethora.integration.vanilla.registry.VanillaMetaRegistration;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.util.registry.Registry.*;

public final class Registration {
    public static final String MOD_ID = Plethora.MOD_ID;

    public static final EntityType<LaserEntity> LASER_ENTITY = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(MOD_ID, "laser"),
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
            ModScreens.NEURAL_INTERFACE_HANDLER_TYPE
        };
        Plethora.LOG.trace("oh no:" + (o[0] != null ? "yes" : "NullPointerException")); // lig was here

        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "kinetic"), KineticRecipe.SERIALIZER);
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "laser"), LaserRecipe.SERIALIZER);

        Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "neural_interface"),
            ModScreens.NEURAL_INTERFACE_HANDLER_TYPE);

        PlethoraEvents.REGISTER.register(api -> {
            // Vanilla registration
            VanillaConverterRegistration.registerConverters(api.converterRegistry());
            VanillaMetaRegistration.registerMetaProviders(api.metaRegistry());

            // Plethora registration
            PlethoraMetaRegistration.registerMetaProviders(api.metaRegistry());
            PlethoraMethodRegistration.registerMethods(api.methodRegistry());

            IModuleRegistry moduleRegistry = api.moduleRegistry();
            moduleRegistry.registerTurtleUpgrade(new ItemStack(ModItems.LASER_MODULE, 1));
            moduleRegistry.registerTurtleUpgrade(new ItemStack(ModItems.SCANNER_MODULE, 1));
            moduleRegistry.registerTurtleUpgrade(new ItemStack(ModItems.SENSOR_MODULE, 1));

            ComputerCraftAPI.registerPeripheralProvider(new ManipulatorPeripheral());

            // TODO: Pocket upgrades
            // TODO: Introspection, creative chat
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
            if (blockEntity instanceof BaseBlockEntity base) {
                base.onChunkUnloaded();
            }
        });
    }

    public static final class ModItems {
        private static final ItemGroup itemGroup = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "main"),
            () -> new ItemStack(ModItems.NEURAL_CONNECTOR) // TODO: Use the neural interface here
        );

        public static final NeuralConnectorItem NEURAL_CONNECTOR =
            register("neural_connector", new NeuralConnectorItem(properties().maxCount(1)));
        public static final NeuralInterfaceItem NEURAL_INTERFACE =
            register("neural_interface", new NeuralInterfaceItem(properties().maxCount(1)));

        public static final KeyboardModuleItem KEYBOARD_MODULE = registerModule("keyboard", KeyboardModuleItem::new);
        public static final KineticModuleItem KINETIC_MODULE = registerModule("kinetic", KineticModuleItem::new);
        public static final LaserModuleItem LASER_MODULE = registerModule("laser", LaserModuleItem::new);
        public static final ScannerModuleItem SCANNER_MODULE = registerModule("scanner", ScannerModuleItem::new);
        public static final SensorModuleItem SENSOR_MODULE = registerModule("sensor", SensorModuleItem::new);

        public static final BlockItem MANIPULATOR_MARK_1 = ofBlock(ModBlocks.MANIPULATOR_MARK_1, BlockItem::new);
        public static final BlockItem MANIPULATOR_MARK_2 = ofBlock(ModBlocks.MANIPULATOR_MARK_2, BlockItem::new);

        private static Item.Settings properties() {
            return new Item.Settings().group(itemGroup);
        }

        private static <B extends Block, I extends Item> I ofBlock(B parent, BiFunction<B, Item.Settings, I> supplier) {
            return Registry.register(ITEM, BLOCK.getId(parent), supplier.apply(parent, properties()));
        }

        private static <T extends Item> T register(String id, T item) {
            return Registry.register(ITEM, new Identifier(MOD_ID, id), item);
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

        private static <T extends Block> T register(String id, T value) {
            return Registry.register(BLOCK, new Identifier(MOD_ID, id), value);
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

        private static <T extends BlockEntity> BlockEntityType<T> ofBlock(Block block, String id,
                                                                          BiFunction<BlockPos, BlockState, T> factory) {
            BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create(factory::apply, block).build();
            return Registry.register(BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, id), blockEntityType);
        }
    }

    public static final class ModScreens {
        public static final ExtendedScreenHandlerType<NeuralInterfaceScreenHandler> NEURAL_INTERFACE_HANDLER_TYPE =
            new ExtendedScreenHandlerType<>(NeuralInterfaceScreenFactory::fromPacket);
    }
}
