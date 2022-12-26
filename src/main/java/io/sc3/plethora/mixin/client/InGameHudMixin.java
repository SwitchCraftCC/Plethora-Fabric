package pw.switchcraft.plethora.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method= "render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
        at={
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;renderSpectatorMenu(Lnet/minecraft/client/util/math/MatrixStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"),
        }
    )
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        this.client.getProfiler().push("plethora:renderCanvas2DOverlay");
        CanvasHandler.render2DOverlay(client, matrices);
        this.client.getProfiler().pop();
    }
}
