package pw.switchcraft.plethora.gameplay.modules.kinetic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;
import pw.switchcraft.plethora.mixin.ServerPlayNetworkHandlerAdapter;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;
import static pw.switchcraft.plethora.util.config.Config.Kinetic.*;

public class KineticModuleItem extends ModuleItem {
    private static final Identifier MODULE_ID = new Identifier(MOD_ID, "module_kinetic");

    private static final int MAX_TICKS = 72000;
    private static final int USE_TICKS = 30;

    private static final double TERMINAL_VELOCITY = -2;

    public KineticModuleItem(Settings settings) {
        super("kinetic", settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MAX_TICKS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // TODO: Check module blacklist here

        player.setCurrentHand(hand);
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity player, int remainingUseTicks) {
        if (world.isClient) return;
        // TODO: Check module blacklist here

        float ticks = MAX_TICKS - remainingUseTicks;
        if (ticks > USE_TICKS) ticks = USE_TICKS;
        if (ticks < 0) ticks = 0;

        launch(player, player.getYaw(), player.getPitch(), (ticks / USE_TICKS) * launchMax);
    }

    public static void launch(Entity entity, float yaw, float pitch, float power) {
        float motionX = -MathHelper.sin(yaw / 180.0f * (float) Math.PI) * MathHelper.cos(pitch / 180.0f * (float) Math.PI);
        float motionZ = MathHelper.cos(yaw / 180.0f * (float) Math.PI) * MathHelper.cos(pitch / 180.0f * (float) Math.PI);
        float motionY = -MathHelper.sin(pitch / 180.0f * (float) Math.PI);

        power /= MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        if (entity instanceof LivingEntity living && living.isFallFlying()) {
            power *= launchElytraScale;
        }

        entity.addVelocity(motionX * power, motionY * power * launchYScale, motionZ * power);
        entity.velocityModified = true; // Equivalent to velocityChanged, sends an EntityVelocityUpdateS2CPacket

        if (launchFallReset && motionY > 0) {
            double entityVelY = entity.getVelocity().y;
            if (entityVelY > 0) {
                entity.fallDistance = 0;
            } else if (entityVelY > TERMINAL_VELOCITY) {
                entity.fallDistance *= entityVelY / TERMINAL_VELOCITY;
            }
        }

        if (launchFloatReset && entity instanceof ServerPlayerEntity spe) {
            ((ServerPlayNetworkHandlerAdapter) spe.networkHandler).setFloatingTicks(0);
        }
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return MODULE_ID;
    }
}
