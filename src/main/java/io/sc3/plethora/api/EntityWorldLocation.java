package io.sc3.plethora.api;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.ToStringBuilder;
import io.sc3.plethora.api.reference.ConstantReference;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EntityWorldLocation implements ConstantReference<IWorldLocation>, IWorldLocation {
    private final Entity entity;

    public EntityWorldLocation(Entity entity) {
        Objects.requireNonNull(entity, "entity cannot be null");
        this.entity = entity;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return entity.getEntityWorld();
    }

    @Nonnull
    @Override
    public BlockPos getPos() {
        Vec3d eyePos = entity.getEyePos();
        return BlockPos.ofFloored(eyePos.x, eyePos.y, eyePos.z);
    }

    @Nonnull
    @Override
    public Vec3d getLoc() {
        return entity.getEyePos();
    }

    @Nonnull
    @Override
    public Box getBounds() {
        // TODO: Necessary to include the collision one here? This is only used for .suck() as far as I can tell
        return entity.getBoundingBox();
    }

    @Nonnull
    @Override
    public IWorldLocation get() throws LuaException {
        return this;
    }

    @Nonnull
    @Override
    public IWorldLocation safeGet() {
        return new WorldLocation(getWorld(), getLoc());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("entity", entity)
            .append("world", getWorld())
            .append("loc", getLoc())
            .toString();
    }
}
