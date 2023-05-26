package io.sc3.plethora.integration.vanilla.method;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import io.sc3.plethora.api.method.ArgumentHelper;
import io.sc3.plethora.api.method.FutureMethodResult;
import io.sc3.plethora.api.method.IUnbakedContext;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.api.module.SubtargetedModuleMethod;
import io.sc3.plethora.gameplay.PlethoraFakePlayer;
import io.sc3.plethora.gameplay.modules.kinetic.KineticMethods;
import io.sc3.plethora.gameplay.modules.kinetic.KineticMethods.KineticMethodContext;
import io.sc3.plethora.integration.PlayerInteractionHelpers;
import io.sc3.plethora.util.PlayerHelpers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import static io.sc3.plethora.gameplay.registry.PlethoraModules.KINETIC_M;
import static io.sc3.plethora.util.Helpers.normaliseAngle;

public final class EntityKineticMethods {
    private static final EnumSet<PositionFlag> LOOK_FLAGS = EnumSet.of(
        PositionFlag.X,
        PositionFlag.Y,
        PositionFlag.Z
    );

    public static final SubtargetedModuleMethod<LivingEntity> LOOK = SubtargetedModuleMethod.of(
        "look", KINETIC_M, LivingEntity.class,
        "function(yaw:number, pitch:number) -- Look in a set direction",
        EntityKineticMethods::look
    );
    private static FutureMethodResult look(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                           @Nonnull IArguments args) throws LuaException {
        LivingEntity entity = KineticMethods.getContext(unbaked).entity();

        final float yaw = (float) normaliseAngle(args.getFiniteDouble(0));
        final float pitch = (float) MathHelper.clamp(normaliseAngle(args.getFiniteDouble(1)), -90, 90);

        if (entity instanceof ServerPlayerEntity spe) {
            Vec3d pos = spe.getPos();
            spe.networkHandler.requestTeleport(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch, LOOK_FLAGS);
        } else {
            entity.setYaw(yaw);
            entity.setBodyYaw(yaw);
            entity.setHeadYaw(yaw);
            entity.setPitch(pitch);
        }

        return FutureMethodResult.empty();
    }

    public static final SubtargetedModuleMethod<LivingEntity> USE = SubtargetedModuleMethod.of(
        "use", KINETIC_M, LivingEntity.class,
        "function([duration:integer], [hand:string]):boolean, string|nil -- Right click with this item using a " +
        "particular hand (\"main\" or \"off\"). The duration is in ticks, or 1/20th of a second.",
        EntityKineticMethods::use
    );
    private static FutureMethodResult use(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                          @Nonnull IArguments args) throws LuaException {
        KineticMethodContext ctx = KineticMethods.getContext(unbaked);
        KineticMethods.KineticMethodPlayer playerCtx = KineticMethods.getPlayer(ctx);
        ServerPlayerEntity player = playerCtx.player();
        PlethoraFakePlayer fakePlayer = playerCtx.fakePlayer();

        int duration = args.optInt(0, 0);
        Hand hand = ArgumentHelper.optHand(args, 1);

        try {
            HitResult hit = PlayerHelpers.raycast(player);
            return PlayerInteractionHelpers.use(player, hit, hand, duration);
        } finally {
            player.clearActiveItem();
            if (fakePlayer != null) fakePlayer.updateCooldown();
        }
    }

    public static final SubtargetedModuleMethod<LivingEntity> SWING = SubtargetedModuleMethod.of(
        "swing", KINETIC_M, LivingEntity.class,
        "function():boolean, string|nil -- Left click with the item in the main hand. Returns the action taken.",
        EntityKineticMethods::swing
    );
    private static FutureMethodResult swing(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                          @Nonnull IArguments args) throws LuaException {
        KineticMethodContext ctx = KineticMethods.getContext(unbaked);
        KineticMethods.KineticMethodPlayer playerCtx = KineticMethods.getPlayer(ctx);
        ServerPlayerEntity player = playerCtx.player();
        PlethoraFakePlayer fakePlayer = playerCtx.fakePlayer();

        try {
            HitResult baseHit = PlayerHelpers.raycast(player);

            switch (baseHit.getType()) {
                case ENTITY -> {
                    EntityHitResult hit = (EntityHitResult) baseHit;
                    Pair<Boolean, String> result = PlayerInteractionHelpers.attack(player, hit.getEntity());
                    return FutureMethodResult.result(result.getLeft(), result.getRight());
                }
                case BLOCK -> {
                    BlockHitResult hit = (BlockHitResult) baseHit;

                    if (fakePlayer != null) {
                        Pair<Boolean, String> result = fakePlayer.dig(hit.getBlockPos(), hit.getSide());
                        return FutureMethodResult.result(result.getLeft(), result.getRight());
                    } else {
                        return FutureMethodResult.result(false, "Nothing to do here");
                    }
                }
                default -> {
                    return FutureMethodResult.result(false, "Nothing to do here");
                }
            }
        } finally {
            player.clearActiveItem();
            if (fakePlayer != null) fakePlayer.updateCooldown();
        }
    }
}
