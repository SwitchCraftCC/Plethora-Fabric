package io.sc3.plethora.integration.vanilla.meta.entity;

import io.sc3.plethora.util.VelocityDeterminable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import io.sc3.plethora.api.IWorldLocation;
import io.sc3.plethora.api.meta.BaseMetaProvider;
import io.sc3.plethora.api.method.IPartialContext;
import io.sc3.plethora.util.EntityHelpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static io.sc3.plethora.api.method.ContextKeys.ORIGIN;
import static io.sc3.plethora.util.Helpers.normaliseAngle;

public class EntityMeta extends BaseMetaProvider<Entity> {
    private EnumSet<Direction.Axis> ALL_AXES = EnumSet.allOf(Direction.Axis.class);

    public EntityMeta() {
        super("Provides some basic information about an entity, such as their their UUID and name.");
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull IPartialContext<Entity> ctx) {
        Entity entity = ctx.getTarget();
        IWorldLocation location = ctx.getContext(ORIGIN, IWorldLocation.class);

        Map<String, Object> result = getBasicProperties(entity, location);

        {
            Map<String, Double> subBlock = new HashMap<>();
            result.put("withinBlock", subBlock);

            Vec3d pos = entity.getEyePos().subtract(entity.getEyePos().floorAlongAxes(ALL_AXES));
            subBlock.put("x", pos.x);
            subBlock.put("y", pos.y);
            subBlock.put("z", pos.z);
        }

        return result;
    }

    public static HashMap<String, Object> getBasicProperties(@Nonnull Entity entity, @Nullable IWorldLocation location) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", entity.getUuid().toString());

        // TODO: Is this a good idea to add? In 1.12, block IDs are returned but not entity IDs, so you have to do some
        //   guesswork based on the names of the entity.
        result.put("key", EntityHelpers.getKey(entity));

        result.put("name", EntityHelpers.getName(entity));
        result.put("displayName", entity.getName().getString());

        Vec3d motion = ((VelocityDeterminable)entity).getMotion();
        result.put("motionX", motion.x);
        result.put("motionY", motion.y);
        result.put("motionZ", motion.z);

        result.put("pitch", normaliseAngle(entity.getPitch()));
        result.put("yaw", normaliseAngle(entity.getYaw()));

        if (location != null && location.getWorld() == entity.getEntityWorld()) {
            Vec3d pos = entity.getEyePos().subtract(location.getLoc());
            result.put("x", pos.x);
            result.put("y", pos.y);
            result.put("z", pos.z);
        }

        return result;
    }
}
