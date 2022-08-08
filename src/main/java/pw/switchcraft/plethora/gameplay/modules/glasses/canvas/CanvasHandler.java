package pw.switchcraft.plethora.gameplay.modules.glasses.canvas;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasUpdatePacket;
import pw.switchcraft.plethora.gameplay.neural.NeuralHelpers;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler.MODULE_DATA;
import static pw.switchcraft.plethora.gameplay.registry.Packets.*;
import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.GLASSES_S;

public class CanvasHandler {
    public static final int ID_2D = 0;
    public static final int ID_3D = 1;

    public static final int WIDTH = 512;
    public static final int HEIGHT = 512 / 16 * 9;

    private static final AtomicInteger id = new AtomicInteger(0);
    private static final HashSet<CanvasServer> server = new HashSet<>();

    private static final Int2ObjectMap<CanvasClient> client = new Int2ObjectOpenHashMap<>();

    private CanvasHandler() {
    }

    public static int nextId() {
        return id.getAndIncrement();
    }

    public static void addServer(CanvasServer canvas) {
        synchronized (server) {
            server.add(canvas);
            ServerPlayNetworking.send(canvas.getPlayer(), CANVAS_ADD_PACKET_ID, canvas.getAddPacket().toBytes());
        }
    }

    public static void removeServer(CanvasServer canvas) {
        synchronized (server) {
            server.remove(canvas);
            ServerPlayNetworking.send(canvas.getPlayer(), CANVAS_REMOVE_PACKET_ID, canvas.getRemovePacket().toBytes());
        }
    }

    public static void addClient(CanvasClient canvas) {
        synchronized (client) {
            client.put(canvas.id, canvas);
        }
    }

    public static void removeClient(CanvasClient canvas) {
        synchronized (client) {
            client.remove(canvas.id);
        }
    }

    public static CanvasClient getClient(int id) {
        synchronized (client) {
            return client.get(id);
        }
    }

    public static void clear() {
        synchronized (server) {
            server.clear();
        }
        synchronized (client) {
            client.clear();
        }
    }

    public static void update(MinecraftServer ignored) {
        synchronized (server) {
            for (CanvasServer canvas : server) {
                CanvasUpdatePacket packet = canvas.getUpdatePacket();
                if (packet != null) {
                    ServerPlayNetworking.send(canvas.getPlayer(), CANVAS_UPDATE_PACKET_ID, packet.toBytes());
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static CanvasClient getCanvas(MinecraftClient client) {
        PlayerEntity player = client.player;

        Optional<ItemStack> optStack = NeuralHelpers.getStack(player);
        if (optStack.isEmpty()) return null;
        ItemStack stack = optStack.get();

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(MODULE_DATA, NbtType.COMPOUND)) return null;

        NbtCompound modules = nbt.getCompound(MODULE_DATA);
        if (!modules.contains(GLASSES_S, NbtType.COMPOUND)) return null;

        NbtCompound data = modules.getCompound(GLASSES_S);
        if (!data.contains("id", NbtType.NUMBER)) return null;

        int id = data.getInt("id");
        return getClient(id);
    }

    public static void render2DOverlay(MinecraftClient client, MatrixStack matrices) {
        CanvasClient canvas = getCanvas(client);
        if (canvas == null) return;

        // If we've no text renderer then we're probably not quite ready yet
        if (client.textRenderer == null) return;

        matrices.push();

        // The hotbar renders at -90 (see InGameGui#renderHotbar)
        matrices.translate(0, 0, -100);
        matrices.scale((float) client.getWindow().getScaledWidth() / WIDTH, (float) client.getWindow().getScaledHeight() / HEIGHT, 2);

        synchronized (canvas) {
            canvas.drawChildren(canvas.getChildren(ID_2D).iterator(), matrices, null);
        }

        RenderSystem.enableTexture();
        RenderSystem.enableCull();

        matrices.pop();
    }

    private static void onWorldRender(WorldRenderContext ctx) {
        CanvasClient canvas = getCanvas(MinecraftClient.getInstance());
        if (canvas == null) return;

        synchronized (canvas) {
            canvas.drawChildren(canvas.getChildren(ID_3D).iterator(), ctx.matrixStack(), ctx.consumers());
        }

        // TODO: GL state
    }

    public static void registerServerEvents() {
        ServerTickEvents.START_SERVER_TICK.register(CanvasHandler::update);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientEvents() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(CanvasHandler::onWorldRender);
    }
}
