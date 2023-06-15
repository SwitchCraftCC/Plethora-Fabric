package io.sc3.plethora.mixin.client;

import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method= "render(Lnet/minecraft/client/gui/DrawContext;F)V",
        at={
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"),
        }
    )
    private void render(DrawContext ctx, float tickDelta, CallbackInfo ci) {
        this.client.getProfiler().push("plethora:renderCanvas2DOverlay");
        CanvasHandler.render2DOverlay(client, ctx);
        this.client.getProfiler().pop();
    }
}
