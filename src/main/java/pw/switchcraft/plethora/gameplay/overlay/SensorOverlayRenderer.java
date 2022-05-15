package pw.switchcraft.plethora.gameplay.overlay;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pw.switchcraft.plethora.util.config.Config.Sensor.entityColours;

public class SensorOverlayRenderer extends FlareOverlayRenderer {
    private static final Map<EntityType<?>, FlareColor> entityColorCache = new HashMap<>();

    public static void render(
        ClientPlayerEntity player,
        ItemStack stack,
        MatrixStack matrices,
        float ticks,
        Camera camera
    ) {
        initFlareRenderer(matrices, camera);

        World world = player.getWorld();
        Vec3d position = player.getEyePos();
        int range = LevelableModuleItem.getEffectiveRange(stack);

        // TODO: Rate limit scanning for these too?
        List<LivingEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), new Box(
            position.x - range, position.y - range, position.z - range,
            position.x + range, position.y + range, position.z + range
        ), e -> e != player);

        for (LivingEntity entity : entities) {
            Vec3d pos = entity.getPos();
            FlareColor color = getFlareColorByEntity(entity.getType());
            renderFlare(matrices, camera, ticks, pos.x, pos.y + entity.getHeight() / 2, pos.z, color, 1.0f);
        }

        uninitFlareRenderer(matrices);
    }

    private static FlareColor getFlareColorByEntity(EntityType<?> entityType) {
        if (entityColorCache.containsKey(entityType)) return entityColorCache.get(entityType);

        Identifier id = Registry.ENTITY_TYPE.getId(entityType);
        FlareColor color = getFlareColorById(entityColours, id);

        entityColorCache.put(entityType, color);
        return color;
    }
}
