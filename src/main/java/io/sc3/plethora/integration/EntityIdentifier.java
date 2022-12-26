package io.sc3.plethora.integration;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import io.sc3.plethora.api.IPlayerOwnable;
import io.sc3.plethora.api.reference.ConstantReference;
import io.sc3.plethora.util.EntityHelpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class EntityIdentifier implements ConstantReference<EntityIdentifier> {
    private final UUID uuid;
    private final String name;

    EntityIdentifier(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public EntityIdentifier(Entity entity) {
        uuid = entity.getUuid();
        name = null;
    }

    @Nonnull
    public UUID getId() {
        return uuid;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public EntityIdentifier get() {
        return this;
    }

    @Nonnull
    @Override
    public EntityIdentifier safeGet() {
        return this;
    }

    public LivingEntity getEntity(MinecraftServer server) throws LuaException {
        Entity entity = EntityHelpers.getEntityFromUuid(server, getId());
        if (!(entity instanceof LivingEntity)) throw new LuaException("Cannot find entity");
        return (LivingEntity) entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityIdentifier that = (EntityIdentifier) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name);
    }

    public static class Player extends EntityIdentifier implements IPlayerOwnable {
        private final GameProfile profile;

        public Player(GameProfile profile) {
            super(profile.getId(), profile.getName());
            this.profile = profile;
        }

        @Override
        public GameProfile getOwningProfile() {
            return profile;
        }

        public ServerPlayerEntity getPlayer(MinecraftServer server) throws LuaException {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(profile.getId());
            if (player == null) throw new LuaException("Player is not online");
            return player;
        }
    }
}
