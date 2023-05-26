package io.sc3.plethora.gameplay.modules.kinetic;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import io.sc3.plethora.api.IPlayerOwnable;
import io.sc3.plethora.api.method.FutureMethodResult;
import io.sc3.plethora.api.method.IContext;
import io.sc3.plethora.api.method.IUnbakedContext;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.api.module.SubtargetedModuleMethod;
import io.sc3.plethora.gameplay.PlethoraFakePlayer;
import io.sc3.plethora.mixin.ServerPlayNetworkHandlerAdapter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static io.sc3.plethora.Plethora.config;
import static io.sc3.plethora.api.method.ArgumentExt.assertDoubleBetween;
import static io.sc3.plethora.api.method.ContextKeys.ORIGIN;
import static io.sc3.plethora.core.ContextHelpers.fromContext;
import static io.sc3.plethora.core.ContextHelpers.fromSubtarget;
import static io.sc3.plethora.gameplay.registry.PlethoraModules.KINETIC_M;
import static io.sc3.plethora.util.Helpers.normaliseAngle;

public class KineticMethods {
    private static final double TERMINAL_VELOCITY = -2;

    public static final SubtargetedModuleMethod<LivingEntity> LAUNCH = SubtargetedModuleMethod.of(
        "launch", KINETIC_M, LivingEntity.class,
        "function(yaw:number, pitch:number, power:number) -- Launch the entity in a set direction",
        KineticMethods::launch
    );
    private static FutureMethodResult launch(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                             @Nonnull IArguments args) throws LuaException {
        final float yaw = (float) normaliseAngle(args.getFiniteDouble(0));
        final float pitch = (float) normaliseAngle(args.getFiniteDouble(1));
        final float power = (float) assertDoubleBetween(args, 2, 0, config.kinetic.launchMax, "Power out of range (%s).");

        return unbaked.getCostHandler().await(power * config.kinetic.launchCost, FutureMethodResult.nextTick(() -> {
            LivingEntity entity = unbaked.bake().getContext(ORIGIN, LivingEntity.class);
            launch(entity, yaw, pitch, power);
            return FutureMethodResult.empty();
        }));
    }

    public static void launch(Entity entity, float yaw, float pitch, float power) {
        float motionX = -MathHelper.sin(yaw / 180.0f * (float) Math.PI) * MathHelper.cos(pitch / 180.0f * (float) Math.PI);
        float motionZ = MathHelper.cos(yaw / 180.0f * (float) Math.PI) * MathHelper.cos(pitch / 180.0f * (float) Math.PI);
        float motionY = -MathHelper.sin(pitch / 180.0f * (float) Math.PI);

        power /= MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        if (entity instanceof LivingEntity living && living.isFallFlying()) {
            power *= config.kinetic.launchElytraScale;
        }

        entity.addVelocity(motionX * power, motionY * power * config.kinetic.launchYScale, motionZ * power);
        entity.velocityModified = true; // Equivalent to velocityChanged, sends an EntityVelocityUpdateS2CPacket

        if (config.kinetic.launchFallReset && motionY > 0) {
            double entityVelY = entity.getVelocity().y;
            if (entityVelY > 0) {
                entity.fallDistance = 0;
            } else if (entityVelY > TERMINAL_VELOCITY) {
                entity.fallDistance *= entityVelY / TERMINAL_VELOCITY;
            }
        }

        if (config.kinetic.launchFloatReset && entity instanceof ServerPlayerEntity spe) {
            ((ServerPlayNetworkHandlerAdapter) spe.networkHandler).setFloatingTicks(0);
        }
    }

    public record KineticMethodContext(IContext<IModuleContainer> context, LivingEntity entity,
                                       @Nullable IPlayerOwnable ownable) {}
    public static KineticMethodContext getContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IContext<IModuleContainer> ctx = unbaked.bake();
        LivingEntity entity = fromSubtarget(ctx, LivingEntity.class);
        IPlayerOwnable ownable = fromContext(ctx, IPlayerOwnable.class, ORIGIN);
        return new KineticMethodContext(ctx, entity, ownable);
    }

    public record KineticMethodPlayer(ServerWorld world, ServerPlayerEntity player, PlethoraFakePlayer fakePlayer) {}
    public static KineticMethodPlayer getPlayer(@Nonnull KineticMethodContext ctx) throws LuaException {
        LivingEntity entity = ctx.entity();
        IPlayerOwnable ownable = ctx.ownable();

        if (!(entity.getEntityWorld() instanceof ServerWorld world)) {
            throw new LuaException("Cannot run on client");
        }

        ServerPlayerEntity player;
        PlethoraFakePlayer fakePlayer;
        if (entity instanceof ServerPlayerEntity spe) {
            player = spe;
            fakePlayer = null;
        } else if (entity instanceof PlayerEntity) {
            throw new LuaException("An unexpected player was used");
        } else {
            if (ownable == null) throw new LuaException("Could not determine owner");
            GameProfile profile = ownable.getOwningProfile();
            if (profile == null) throw new LuaException("Could not determine owner profile");
            player = fakePlayer = new PlethoraFakePlayer(world, entity, ownable.getOwningProfile());
        }

        return new KineticMethodPlayer(world, player, fakePlayer);
    }
}
