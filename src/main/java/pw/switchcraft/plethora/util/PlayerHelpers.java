package pw.switchcraft.plethora.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import pw.switchcraft.plethora.api.IPlayerOwnable;

import javax.annotation.Nullable;

public class PlayerHelpers {
    @Nullable
    public static GameProfile getProfile(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            return player.getGameProfile();
        } else if (entity instanceof IPlayerOwnable playerOwnable) {
            return playerOwnable.getOwningProfile();
        } else {
            return null;
        }
    }
}
