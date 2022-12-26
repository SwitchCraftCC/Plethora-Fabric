package io.sc3.plethora.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.sc3.plethora.gameplay.PlethoraFakePlayer;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin {
    @Shadow @Final protected PathAwareEntity mob;

    @Inject(method = "shouldContinue()Z", at = @At("HEAD"), cancellable = true)
    public void shouldContinue(CallbackInfoReturnable<Boolean> cir) {
        // Prevent mobs from targeting our fake player
        LivingEntity entity = mob.getTarget();
        if (entity instanceof PlethoraFakePlayer) {
            cir.setReturnValue(false);
        }
    }
}
