package io.sc3.plethora.integration.vanilla.meta.entity;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import io.sc3.plethora.api.meta.BasicMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class PlayerEntityMeta extends BasicMetaProvider<PlayerEntity> {
    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull PlayerEntity player) {
        Map<String, Object> out = new HashMap<>();

        HungerManager h = player.getHungerManager();
        Map<String, Object> foodMap = new HashMap<>();
        out.put("food", foodMap);
        foodMap.put("hunger", h.getFoodLevel());
        foodMap.put("saturation", h.getSaturationLevel());
        foodMap.put("hungry", h.isNotFull());

        out.put("heldItemSlot", player.getInventory().selectedSlot);

        return out;
    }
}
