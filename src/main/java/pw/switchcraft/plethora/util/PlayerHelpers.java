package pw.switchcraft.plethora.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import pw.switchcraft.plethora.api.IPlayerOwnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

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

    @Nullable
    public static GameProfile readProfile(@Nonnull NbtCompound nbt) {
        if (!nbt.contains("owner", NbtCompound.COMPOUND_TYPE)) {
            return null;
        }

        NbtCompound owner = nbt.getCompound("owner");
        return new GameProfile(
            new UUID(owner.getLong("upper_id"), owner.getLong("lower_id")),
            owner.getString("name")
        );
    }

    public static void writeProfile(@Nonnull NbtCompound nbt, @Nullable GameProfile profile) {
        if (profile == null) {
            nbt.remove("owner");
        } else {
            NbtCompound owner = new NbtCompound();
            nbt.put("owner", owner);

            owner.putLong("upper_id", profile.getId().getMostSignificantBits());
            owner.putLong("lower_id", profile.getId().getLeastSignificantBits());
            owner.putString("name", profile.getName());
        }
    }
}
