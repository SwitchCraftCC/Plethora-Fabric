package io.sc3.plethora.gameplay.client;

import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.sc3.plethora.core.TurtleUpgradeModuleRenderer;
import io.sc3.plethora.gameplay.client.block.ManipulatorOutlineRenderer;
import io.sc3.plethora.gameplay.client.block.ManipulatorRenderer;
import io.sc3.plethora.gameplay.client.entity.LaserRenderer;
import io.sc3.plethora.gameplay.client.gui.NeuralInterfaceScreen;
import io.sc3.plethora.gameplay.client.neural.NeuralInterfaceTrinketRenderer;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler;
import io.sc3.plethora.gameplay.modules.glasses.networking.CanvasAddPacket;
import io.sc3.plethora.gameplay.modules.glasses.networking.CanvasRemovePacket;
import io.sc3.plethora.gameplay.modules.glasses.networking.CanvasUpdatePacket;
import io.sc3.plethora.gameplay.modules.keyboard.ClientKeyListener;
import io.sc3.plethora.gameplay.modules.keyboard.KeyboardComputerScreen;
import io.sc3.plethora.gameplay.modules.keyboard.KeyboardListenPacket;
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler;
import io.sc3.plethora.gameplay.registry.Registration;
import io.sc3.plethora.gameplay.registry.Registration.ModBlockEntities;
import io.sc3.plethora.gameplay.registry.Registration.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import static io.sc3.library.networking.ScLibraryPacketKt.registerClientReceiver;
import static io.sc3.plethora.Plethora.log;

@Environment(EnvType.CLIENT)
public class PlethoraClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        log.info("Initializing client...");

        // Renderers
        EntityRendererRegistry.register(Registration.LASER_ENTITY, LaserRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MANIPULATOR_MARK_1, ctx -> new ManipulatorRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.MANIPULATOR_MARK_2, ctx -> new ManipulatorRenderer());
        TrinketRendererRegistry.registerRenderer(Registration.ModItems.NEURAL_INTERFACE, new NeuralInterfaceTrinketRenderer());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(Registration.ModTurtleUpgradeSerialisers.MODULE, TurtleUpgradeModuleRenderer.INSTANCE);
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(Registration.ModTurtleUpgradeSerialisers.KINETIC_AUGMENT, TurtleUpgradeModuleRenderer.INSTANCE::getModel);

        // These generics are required even if IDEA says they're not
        //noinspection RedundantTypeArguments
        HandledScreens.<NeuralInterfaceScreenHandler, NeuralInterfaceScreen>register(ModScreens.NEURAL_INTERFACE_HANDLER_TYPE, NeuralInterfaceScreen::new);
        HandledScreens.<AbstractComputerMenu, KeyboardComputerScreen<AbstractComputerMenu>>register(ModScreens.KEYBOARD_HANDLER_TYPE, KeyboardComputerScreen::new);

        // Custom packets
        registerClientReceiver(CanvasAddPacket.id, CanvasAddPacket::fromBytes);
        registerClientReceiver(CanvasRemovePacket.id, CanvasRemovePacket::fromBytes);
        registerClientReceiver(CanvasUpdatePacket.id, CanvasUpdatePacket::fromBytes);
        registerClientReceiver(KeyboardListenPacket.id, KeyboardListenPacket::fromBytes);

        // CC:R's ComputerScreenBase makes Screen.init() and other methods final (?!), so we have to call our own init
        // function using this event instead
        // TODO: PR a fix to CC:T and CC:R
        ScreenEvents.AFTER_INIT.register((client, screen, __, ___) -> {
           if (screen instanceof NeuralInterfaceScreen neuralScreen) {
               neuralScreen.initNeural();
           }
        });

        // Custom outline renderer for the manipulator
        WorldRenderEvents.BLOCK_OUTLINE.register(ManipulatorOutlineRenderer::onBlockOutline);

        CanvasHandler.registerClientEvents();
        ClientKeyListener.INSTANCE.registerEvents();
    }
}
