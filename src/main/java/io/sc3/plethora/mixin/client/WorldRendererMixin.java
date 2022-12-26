package io.sc3.plethora.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.sc3.plethora.gameplay.overlay.OverlayRenderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
  @Shadow
  @Final
  private MinecraftClient client;

  @Inject(method = "render", at = @At("TAIL"))
  private void render(
    MatrixStack matrices,
    float tickDelta,
    long limitTime,
    boolean renderBlockOutline,
    Camera camera,
    GameRenderer gameRenderer,
    LightmapTextureManager lightmapTextureManager,
    Matrix4f positionMatrix,
    CallbackInfo ci
  ) {
    this.client.getProfiler().swap("plethora:renderOverlay");
    OverlayRenderer.renderOverlay(this.client, matrices, tickDelta, camera);
  }
}
