package pw.switchcraft.plethora.integration.vanilla.entity;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import pw.switchcraft.plethora.api.meta.BasicMetaProvider;

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

        PlayerAbilities abilities = player.getAbilities();
        out.put("isFlying", abilities.flying);
        out.put("allowFlying", abilities.allowFlying);
        out.put("walkSpeed", abilities.getWalkSpeed());
        out.put("flySpeed", abilities.getFlySpeed());

        return out;
    }
}
