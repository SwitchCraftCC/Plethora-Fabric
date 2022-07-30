package pw.switchcraft.plethora.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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
            new UUID(owner.getLong("id_upper"), owner.getLong("id_lower")),
            owner.getString("name")
        );
    }

    public static void writeProfile(@Nonnull NbtCompound nbt, @Nullable GameProfile profile) {
        if (profile == null) {
            nbt.remove("owner");
        } else {
            NbtCompound owner = new NbtCompound();
            nbt.put("owner", owner);

            owner.putLong("id_upper", profile.getId().getMostSignificantBits());
            owner.putLong("id_lower", profile.getId().getLeastSignificantBits());
            owner.putString("name", profile.getName());
        }
    }

    public static HitResult raycast(ServerPlayerEntity player) {
        return raycast(player, 4.0f); // Default non-creative interaction range
    }

    public static HitResult raycast(ServerPlayerEntity player, float range) {
        float tickDelta = 1.0f;

        // Try to hit a block
        HitResult blockHit = player.raycast(range, tickDelta, false);

        Vec3d vec3d = player.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = player.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range);

        // Try to hit an entity
        Vec3d cam = player.getCameraPosVec(tickDelta);
        Vec3d rotationVec = player.getRotationVec(tickDelta);
        Vec3d end = cam.add(rotationVec.multiply(range));
        Box box = player.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0);

        EntityHitResult entityHit = ProjectileUtil.raycast(player, cam, end, box, e ->
            !e.isSpectator() && e.isCollidable() && e.isLiving(), range * range);

        if (entityHit != null) {
            // Figure out which is closer
            double entityDistance = cam.squaredDistanceTo(entityHit.getPos());
            double blockDistance = blockHit != null ? cam.squaredDistanceTo(blockHit.getPos()) : Double.MAX_VALUE;
            if (blockHit == null || entityDistance < blockDistance) {
                return entityHit;
            } else {
                return blockHit;
            }
        } else {
            return blockHit;
        }
    }
}
