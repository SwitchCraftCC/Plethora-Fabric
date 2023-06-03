package io.sc3.plethora.gameplay.registry;

import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import io.sc3.plethora.Plethora;
import io.sc3.plethora.api.PlethoraEvents;
import io.sc3.plethora.api.module.IModuleHandler;
import io.sc3.plethora.core.PocketUpgradeModule;
import io.sc3.plethora.core.TurtleUpgradeModule;
import io.sc3.plethora.gameplay.BaseBlockEntity;
import io.sc3.plethora.gameplay.data.recipes.handlers.RecipeHandlers;
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlock;
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlockEntity;
import io.sc3.plethora.gameplay.manipulator.ManipulatorPeripheral;
import io.sc3.plethora.gameplay.manipulator.ManipulatorType;
import io.sc3.plethora.gameplay.modules.glasses.GlassesModuleItem;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler;
import io.sc3.plethora.gameplay.modules.introspection.IntrospectionModuleItem;
import io.sc3.plethora.gameplay.modules.keyboard.KeyboardKeyPacket;
import io.sc3.plethora.gameplay.modules.keyboard.KeyboardModuleItem;
import io.sc3.plethora.gameplay.modules.keyboard.ServerKeyListener;
import io.sc3.plethora.gameplay.modules.kinetic.KineticModuleItem;
import io.sc3.plethora.gameplay.modules.kinetic.KineticTurtleUpgrade;
import io.sc3.plethora.gameplay.modules.laser.LaserEntity;
import io.sc3.plethora.gameplay.modules.laser.LaserModuleItem;
import io.sc3.plethora.gameplay.modules.scanner.ScannerModuleItem;
import io.sc3.plethora.gameplay.modules.sensor.SensorModuleItem;
import io.sc3.plethora.gameplay.neural.NeuralConnectorItem;
import io.sc3.plethora.gameplay.neural.NeuralInterfaceItem;
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenFactory;
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler;
import io.sc3.plethora.gameplay.redstone.RedstoneIntegratorBlock;
import io.sc3.plethora.gameplay.redstone.RedstoneIntegratorBlockEntity;
import io.sc3.plethora.gameplay.redstone.RedstoneIntegratorTicker;
import io.sc3.plethora.integration.InternalIntegration;
import io.sc3.plethora.integration.computercraft.registry.ComputerCraftMetaRegistration;
import io.sc3.plethora.integration.computercraft.registry.ComputerCraftMethodRegistration;
import io.sc3.plethora.integration.vanilla.registry.VanillaConverterRegistration;
import io.sc3.plethora.integration.vanilla.registry.VanillaMetaRegistration;
import io.sc3.plethora.integration.vanilla.registry.VanillaMethodRegistration;
import io.sc3.plethora.integration.vanilla.registry.VanillaPeripheralRegistration;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.sc3.library.networking.ScLibraryPacketKt.registerServerReceiver;
import static io.sc3.plethora.Plethora.log;
import static net.minecraft.registry.Registries.*;

public final class Registration {
  private static final List<Item> items = new ArrayList<>();

  public static final EntityType<LaserEntity> LASER_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Plethora.modId, "laser"),
    FabricEntityTypeBuilder.<LaserEntity>create(SpawnGroup.MISC, LaserEntity::new)
      .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
      .trackRangeBlocks(4).trackedUpdateRate(10)
      .forceTrackedVelocityUpdates(true)
      .build()
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
      ModDamageSources.LASER,
    };
    log.trace("oh no:" + (o[0] != null ? "yes" : "NullPointerException")); // lig was here

    Registry.register(Registries.SCREEN_HANDLER, new Identifier(Plethora.modId, "neural_interface"),
      ModScreens.NEURAL_INTERFACE_HANDLER_TYPE);
    Registry.register(Registries.SCREEN_HANDLER, new Identifier(Plethora.modId, "keyboard"),
      ModScreens.KEYBOARD_HANDLER_TYPE);

    PlethoraEvents.REGISTER.register(api -> {
      // Vanilla registration
      VanillaConverterRegistration.registerConverters(api.converterRegistry());
      VanillaMetaRegistration.registerMetaProviders(api.metaRegistry());
      VanillaMethodRegistration.registerMethods(api.methodRegistry());
      VanillaPeripheralRegistration.registerPeripherals();

      // Plethora registration
      PlethoraMetaRegistration.registerMetaProviders(api.metaRegistry());
      PlethoraMethodRegistration.registerMethods(api.methodRegistry());

      // ComputerCraft integration registration
      ComputerCraftMetaRegistration.registerMetaProviders(api.metaRegistry());
      ComputerCraftMethodRegistration.registerMethods(api.methodRegistry());

      VanillaDetailRegistries.ITEM_STACK.addProvider(ItemDetailsProvider.INSTANCE);

      // Other integration registration
      InternalIntegration.Companion.init(api);

      // Manipulator peripheral
      PeripheralLookup.get().registerForBlockEntity(ManipulatorPeripheral::getPeripheral, ModBlockEntities.MANIPULATOR_MARK_1);
      PeripheralLookup.get().registerForBlockEntity(ManipulatorPeripheral::getPeripheral, ModBlockEntities.MANIPULATOR_MARK_2);
      PeripheralLookup.get().registerForBlockEntity((b, s) -> b.getPeripheral(), ModBlockEntities.REDSTONE_INTEGRATOR);
    });

    ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
      if (blockEntity instanceof BaseBlockEntity base) base.onChunkLoaded();
    });

    ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
      if (blockEntity instanceof BaseBlockEntity base) base.onChunkUnloaded();
    });

    registerServerReceiver(KeyboardKeyPacket.id, KeyboardKeyPacket::fromBytes);

    RedstoneIntegratorTicker.registerEvents();
    CanvasHandler.registerServerEvents();
    ServerKeyListener.registerEvents();
    LaserEntity.initLaserTracker();

    RecipeHandlers.registerSerializers();
  }

  public static void bootstrapDamageTypes(Registerable<DamageType> damageTypeRegisterable) {
    damageTypeRegisterable.register(ModDamageSources.LASER, new DamageType("plethora.laser", 0.1f));
  }

  public static final class ModItems {
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

    public static final ItemGroup PLETHORA_ITEM_GROUP = FabricItemGroup.builder(new Identifier(Plethora.modId, "main"))
      .icon(() -> new ItemStack(NEURAL_CONNECTOR))
      .entries((enabledFeatures, entries) -> items.forEach(entries::add))
      .build();

    private static Item.Settings properties() {
      return new Item.Settings();
    }

    private static <B extends Block, I extends Item> I ofBlock(B parent, BiFunction<B, Item.Settings, I> supplier) {
      var item = Registry.register(ITEM, BLOCK.getId(parent), supplier.apply(parent, properties()));
      items.add(item);
      return item;
    }

    private static <T extends Item> T register(String id, T item) {
      var i = Registry.register(ITEM, new Identifier(Plethora.modId, id), item);
      items.add(i);
      return i;
    }

    private static <T extends Item> T registerModule(String id, Function<Item.Settings, T> itemCtor) {
      return register("module_" + id, itemCtor.apply(properties().maxCount(16)));
    }
  }

  public static class ModBlocks {
    public static final Block MANIPULATOR_MARK_1 = register("manipulator_mark_1",
      new ManipulatorBlock(properties().nonOpaque(), ManipulatorType.MARK_1));
    public static final Block MANIPULATOR_MARK_2 = register("manipulator_mark_2",
      new ManipulatorBlock(properties().nonOpaque(), ManipulatorType.MARK_2));
    public static final Block REDSTONE_INTEGRATOR = register("redstone_integrator",
      new RedstoneIntegratorBlock(properties()));

    private static <T extends Block> T register(String id, T value) {
      return Registry.register(BLOCK, new Identifier(Plethora.modId, id), value);
    }

    private static Block.Settings properties() {
      return Block.Settings.of(Material.STONE).strength(2.0F).requiresTool();
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

    public static final ExtendedScreenHandlerType<ComputerMenuWithoutInventory> KEYBOARD_HANDLER_TYPE =
      new ExtendedScreenHandlerType<>((id, inv, data) ->
        new ComputerMenuWithoutInventory(ModScreens.KEYBOARD_HANDLER_TYPE, id, inv, new ComputerContainerData(data)));
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
      PocketUpgradeSerialiser.simpleWithCustomItem((id, item) -> {
        log.info("Registering pocket module {} with crafting item {}", id, item);
        if (item.getItem() instanceof IModuleHandler handler) {
          return new PocketUpgradeModule(item, handler, item.getTranslationKey() + ".adjective");
        } else if (item.isEmpty()) {
          throw new IllegalArgumentException("Cannot register a pocket module (id: " + id + ") with an empty item!");
        } else {
          throw new IllegalArgumentException("Item " + item + " is not a valid module handler!");
        }
      })
    );
  }

  public static final class ModDamageSources {
    public static final RegistryKey<DamageType> LASER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
      new Identifier(Plethora.modId, "laser"));
  }
}
