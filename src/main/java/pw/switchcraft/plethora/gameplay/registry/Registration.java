package pw.switchcraft.plethora.gameplay.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.api.PlethoraEvents;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.IModuleRegistry;
import pw.switchcraft.plethora.gameplay.modules.keyboard.KeyboardModuleItem;
import pw.switchcraft.plethora.gameplay.modules.kinetic.KineticModuleItem;
import pw.switchcraft.plethora.gameplay.modules.kinetic.KineticRecipe;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserEntity;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserMethods;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserModuleItem;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserRecipe;
import pw.switchcraft.plethora.gameplay.modules.scanner.ScannerModuleItem;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorModuleItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralConnectorItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenFactory;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler;

import java.util.function.Function;

import static net.minecraft.util.registry.Registry.ITEM;

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
            ModItems.NEURAL_CONNECTOR,
            ModScreens.NEURAL_INTERFACE_HANDLER_TYPE
        };

        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "kinetic"), KineticRecipe.SERIALIZER);
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "laser"), LaserRecipe.SERIALIZER);

        Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "neural_interface"),
            ModScreens.NEURAL_INTERFACE_HANDLER_TYPE);

        PlethoraEvents.REGISTER.register(api -> {
            api.methodRegistry().registerMethod(
                new Identifier(MOD_ID, "laser_fire"), // TODO: should we just reflect names?
                IModuleContainer.class,
                LaserMethods.FIRE
            );

            IModuleRegistry moduleRegistry = api.moduleRegistry();
            moduleRegistry.registerTurtleUpgrade(new ItemStack(ModItems.LASER_MODULE, 1));
            // TODO: Introspection, scanner, sensor, creative chat
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

        private static Item.Settings properties() {
            return new Item.Settings().group(itemGroup);
        }

        private static <T extends Item> T register(String id, T item) {
            return Registry.register(ITEM, new Identifier(MOD_ID, id), item);
        }

        private static <T extends Item> T registerModule(String id, Function<Item.Settings, T> itemCtor) {
            return register("module_" + id, itemCtor.apply(properties().maxCount(1)));
        }
    }

    public static final class ModScreens {
        public static final ExtendedScreenHandlerType<NeuralInterfaceScreenHandler> NEURAL_INTERFACE_HANDLER_TYPE =
            new ExtendedScreenHandlerType<>(NeuralInterfaceScreenFactory::fromPacket);
    }
}
