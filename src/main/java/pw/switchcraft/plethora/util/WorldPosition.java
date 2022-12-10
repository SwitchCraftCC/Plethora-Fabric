package pw.switchcraft.plethora.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Objects;

public final class WorldPosition {
    private final RegistryKey<World> worldKey;
    private WeakReference<World> world;
    private final Vec3d pos;

    public WorldPosition(@Nonnull World world, @Nonnull Vec3d pos) {
        Objects.requireNonNull(world, "world cannot be null");
        Objects.requireNonNull(pos, "pos cannot be null");

        worldKey = world.getRegistryKey();
        this.world = new WeakReference<>(world);
        this.pos = pos;
    }

    private WorldPosition(RegistryKey<World> worldKey, @Nonnull Vec3d pos) {
        this.worldKey = worldKey;
        world = new WeakReference<>(null);
        this.pos = pos;
    }

    public WorldPosition(@Nonnull World world, @Nonnull BlockPos pos) {
        Objects.requireNonNull(world, "world cannot be null");
        Objects.requireNonNull(pos, "pos cannot be null");

        worldKey = world.getRegistryKey();
        this.world = new WeakReference<>(world);
        this.pos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public WorldPosition(@Nonnull World world, double x, double y, double z) {
        this(world, new Vec3d(x, y, z));
    }

    @Nullable
    public World getWorld() {
        return world.get();
    }

    @Nullable
    public World getWorld(MinecraftServer server) {
        World world = this.world.get();
        World worldByKey = server.getWorld(worldKey);

        if (world == null && worldByKey != null) {
            this.world = new WeakReference<>(world = worldByKey);
        }

        return world;
    }

    public RegistryKey<World> getWorldKey() {
        return worldKey;
    }

    @Nonnull
    public Vec3d getPos() {
        return pos;
    }

    public NbtCompound serializeNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putString("dim", worldKey.toString());
        tag.putDouble("x", pos.x);
        tag.putDouble("y", pos.y);
        tag.putDouble("z", pos.z);
        return tag;
    }

    public static WorldPosition deserializeNbt(NbtCompound nbt) {
        return new WorldPosition(
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(nbt.getString("worldKey"))),
            new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"))
        );
    }
}
