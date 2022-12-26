package io.sc3.plethora.integration.vanilla.meta.entity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import io.sc3.plethora.api.meta.BaseMetaProvider;
import io.sc3.plethora.api.method.ContextHelpers;
import io.sc3.plethora.api.method.IPartialContext;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A basic provider for living entities
 */
public final class LivingEntityMeta extends BaseMetaProvider<LivingEntity> {
    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull IPartialContext<LivingEntity> context) {
        LivingEntity target = context.getTarget();
        Map<String, Object> map = new HashMap<>();

        {
            Map<String, Object> armor = new HashMap<>();
            armor.put("boots", ContextHelpers.wrapStack(context, target.getEquippedStack(EquipmentSlot.FEET)));
            armor.put("leggings", ContextHelpers.wrapStack(context, target.getEquippedStack(EquipmentSlot.LEGS)));
            armor.put("chestplate", ContextHelpers.wrapStack(context, target.getEquippedStack(EquipmentSlot.CHEST)));
            armor.put("helmet", ContextHelpers.wrapStack(context, target.getEquippedStack(EquipmentSlot.HEAD)));
            map.put("armor", armor);
        }

        map.put("heldItem", ContextHelpers.wrapStack(context, target.getMainHandStack()));
        map.put("offhandItem", ContextHelpers.wrapStack(context, target.getOffHandStack()));
        map.put("potionEffects", target.getActiveStatusEffects().keySet().stream()
            .map(s -> s.getName().getString()).collect(Collectors.toList()));

        map.put("health", target.getHealth());
        map.put("maxHealth", target.getMaxHealth());
        map.put("isAirborne", target.velocityDirty);
        map.put("isBurning", target.isOnFire());
        map.put("isAlive", target.isAlive());
        map.put("isInWater", target.isTouchingWater());
        map.put("isOnLadder", target.isClimbing());
        map.put("isSleeping", target.isSleeping());
        map.put("isRiding", target.hasVehicle());
        map.put("isSneaking", target.isSneaking());
        map.put("isSprinting", target.isSprinting());
        map.put("isWet", target.isWet());
        map.put("isChild", target.isBaby());
        map.put("isDead", target.isDead());
        map.put("isElytraFlying", target.isFallFlying());

        return map;
    }
}
