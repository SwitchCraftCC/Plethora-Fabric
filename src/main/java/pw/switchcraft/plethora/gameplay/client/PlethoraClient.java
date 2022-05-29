package pw.switchcraft.plethora.gameplay.client;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.gameplay.client.block.ManipulatorOutlineRenderer;
import pw.switchcraft.plethora.gameplay.client.block.ManipulatorRenderer;
import pw.switchcraft.plethora.gameplay.client.entity.LaserRenderer;
import pw.switchcraft.plethora.gameplay.client.gui.GuiNeuralInterface;
import pw.switchcraft.plethora.gameplay.client.neural.NeuralInterfaceTrinketRenderer;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler;
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasAddPacket;
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasRemovePacket;
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasUpdatePacket;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler;
import pw.switchcraft.plethora.gameplay.registry.Registration;
import pw.switchcraft.plethora.gameplay.registry.Registration.ModBlockEntities;
import pw.switchcraft.plethora.util.EntitySpawnPacket;

import java.util.UUID;

import static pw.switchcraft.plethora.gameplay.registry.Packets.*;

@Environment(EnvType.CLIENT)
public class PlethoraClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Plethora.LOG.info("Initializing client...");

        // Renderers
        EntityRendererRegistry.register(Registration.LASER_ENTITY, LaserRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MANIPULATOR_MARK_1, ManipulatorRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.MANIPULATOR_MARK_2, ManipulatorRenderer::new);
        TrinketRendererRegistry.registerRenderer(Registration.ModItems.NEURAL_INTERFACE, new NeuralInterfaceTrinketRenderer());

        // These generics are required even if IDEA says they're not
        HandledScreens.<NeuralInterfaceScreenHandler, GuiNeuralInterface>register(Registration.ModScreens.NEURAL_INTERFACE_HANDLER_TYPE, GuiNeuralInterface::new);

        // Must register a packet to spawn custom entities, because Fabric API
        ClientPlayNetworking.registerGlobalReceiver(SPAWN_PACKET_ID, (client, handler, buf, responseSender) -> {
            EntityType<?> et = Registry.ENTITY_TYPE.get(buf.readVarInt());
            UUID uuid = buf.readUuid();
            int id = buf.readVarInt();
            Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(buf);
            float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(buf);
            float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(buf);

            client.execute(() -> {
                if (MinecraftClient.getInstance().world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world!");

                Entity e = et.create(MinecraftClient.getInstance().world);
                if (e == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");

                e.updateTrackedPosition(pos);
                e.setPos(pos.x, pos.y, pos.z);
                e.setPitch(pitch);
                e.setYaw(yaw);
                e.setId(id);
                e.setUuid(uuid);

                MinecraftClient.getInstance().world.addEntity(id, e);
            });
        });

        // Custom packets
        ClientPlayNetworking.registerGlobalReceiver(CANVAS_ADD_PACKET_ID, CanvasAddPacket::onReceive);
        ClientPlayNetworking.registerGlobalReceiver(CANVAS_REMOVE_PACKET_ID, CanvasRemovePacket::onReceive);
        ClientPlayNetworking.registerGlobalReceiver(CANVAS_UPDATE_PACKET_ID, CanvasUpdatePacket::onReceive);

        // CC:R's ComputerScreenBase makes Screen.init() and other methods final (?!), so we have to call our own init
        // function using this event instead
        // TODO: PR a fix to CC:T and CC:R
        ScreenEvents.AFTER_INIT.register((client, screen, __, ___) -> {
           if (screen instanceof GuiNeuralInterface neuralScreen) {
               neuralScreen.initNeural();
           }
        });

        // Custom outline renderer for the manipulator
        WorldRenderEvents.BLOCK_OUTLINE.register(ManipulatorOutlineRenderer::onBlockOutline);

        CanvasHandler.registerClientEvents();
    }
}
