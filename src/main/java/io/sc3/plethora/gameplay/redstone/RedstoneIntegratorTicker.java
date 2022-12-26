package pw.switchcraft.plethora.gameplay.redstone;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RedstoneIntegratorTicker {
    private static final Set<RedstoneIntegratorBlockEntity> toTick = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void enqueueTick(RedstoneIntegratorBlockEntity be) {
        toTick.add(be);
    }

    public static void handleTick(MinecraftServer server) {
        Iterator<RedstoneIntegratorBlockEntity> it = toTick.iterator();
        while (it.hasNext()) {
            RedstoneIntegratorBlockEntity be = it.next();
            be.updateOnce();
            it.remove();
        }
    }

    public static void handleUnload(MinecraftServer server, ServerWorld eventWorld) {
        if (eventWorld.isClient) return;
        Iterator<RedstoneIntegratorBlockEntity> it = toTick.iterator();
        while (it.hasNext()) {
            World world = it.next().getWorld();
            if (world == null || world == eventWorld) it.remove();
        }
    }

    public static void registerEvents() {
        ServerTickEvents.START_SERVER_TICK.register(RedstoneIntegratorTicker::handleTick);
        ServerWorldEvents.UNLOAD.register(RedstoneIntegratorTicker::handleUnload);
    }
}
