package pw.switchcraft.plethora.api;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import pw.switchcraft.plethora.api.reference.ConstantReference;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class WorldLocation implements ConstantReference<IWorldLocation>, IWorldLocation {
    private final World world;
    private final BlockPos pos;
    private final Vec3d loc;

    public WorldLocation(@Nonnull World world, @Nonnull BlockPos pos) {
        Objects.requireNonNull(world, "world cannot be null");
        Objects.requireNonNull(pos, "pos cannot be null");

        this.world = world;
        this.pos = pos.toImmutable();
        loc = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public WorldLocation(@Nonnull World world, @Nonnull Vec3d pos) {
        Objects.requireNonNull(world, "world cannot be null");
        Objects.requireNonNull(pos, "pos cannot be null");

        this.world = world;
        this.pos = new BlockPos(pos.x, pos.y + 0.5, pos.z);
        loc = pos;
    }

    @Nonnull
    @Override
    public World getWorld() {
        return world;
    }

    @Nonnull
    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Nonnull
    @Override
    public Vec3d getLoc() {
        return loc;
    }

    @Nonnull
    @Override
    public Box getBounds() {
        throw new NotImplementedException("Not implemented");
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
}
