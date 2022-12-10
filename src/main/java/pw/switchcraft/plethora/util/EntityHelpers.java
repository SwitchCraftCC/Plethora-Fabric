package pw.switchcraft.plethora.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EntityHelpers {
    public static Entity getEntityFromUuid(MinecraftServer server, UUID uuid) {
        for (ServerWorld world : server.getWorlds()) {
            if (world != null) {
                Entity entity = world.getEntity(uuid);
                if (entity != null) return entity;
            }
        }

        return null;
    }

    @Nonnull
    public static String getName(Entity entity) {
        // TODO: Verify this matches the original logic
        if (entity instanceof PlayerEntity) {
            return entity.getName().getString();
        } else {
            return entity.getType().getName().getString();
        }
    }

    @Nonnull
    public static String getKey(Entity entity) {
        return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
    }
}
