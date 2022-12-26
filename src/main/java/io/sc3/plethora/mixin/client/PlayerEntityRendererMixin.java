package io.sc3.plethora.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.sc3.plethora.gameplay.client.entity.SquidFeatureRenderer;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
  @SuppressWarnings("ConstantConditions")
  public PlayerEntityRendererMixin() { super(null, null, 0);}

  @Inject(method="<init>", at=@At("TAIL"))
  public void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
    addFeature(new SquidFeatureRenderer((PlayerEntityRenderer) (Object) this));
  }
}
