package pw.switchcraft.plethora.gameplay.modules.sensor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import pw.switchcraft.plethora.api.IWorldLocation;
import pw.switchcraft.plethora.util.EntityHelpers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class SensorHelpers {
    public static Box getBox(BlockPos pos, int radius) {
        final int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new Box(
            x - radius, y - radius, z - radius,
            x + radius, y + radius, z + radius
        );
    }

    @Nullable
    public static Entity findEntityByUuid(IWorldLocation location, int radius, UUID uuid) {
        List<Entity> entities = location.getWorld().getEntitiesByClass(Entity.class, getBox(location.getPos(), radius),
            entity -> DEFAULT_PREDICATE.test(entity) && entity.getUuid().equals(uuid));
        return entities.isEmpty() ? null : entities.get(0);
    }

    @Nullable
    public static Entity findEntityByName(IWorldLocation location, int radius, String name) {
        List<Entity> entities = location.getWorld().getEntitiesByClass(Entity.class, getBox(location.getPos(), radius),
            entity -> DEFAULT_PREDICATE.test(entity) &&
                (EntityHelpers.getName(entity).equals(name) || EntityHelpers.getKey(entity).equals(name)));
        return entities.isEmpty() ? null : entities.get(0);
    }

    public static final Predicate<Entity> DEFAULT_PREDICATE = entity ->
        entity != null
            && entity.isAlive()
            && (!(entity instanceof PlayerEntity player) || !player.isSpectator());
}
