package pw.switchcraft.plethora.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.switchcraft.plethora.gameplay.PlethoraFakePlayer;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void shouldContinue(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        // Prevent mobs from targeting our fake player
        if (target instanceof PlethoraFakePlayer) {
            cir.setReturnValue(false);
        }
    }
}
