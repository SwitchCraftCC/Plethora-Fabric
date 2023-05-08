package io.sc3.plethora.mixin;

import io.sc3.plethora.util.VelocityDeterminable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(at = @At("TAIL"), method = "tickEntity(Lnet/minecraft/entity/Entity;)V")
    private void setPrevPos(Entity entity, CallbackInfo ci) {
        ((VelocityDeterminable)entity).storePrevPos();
    }
}
