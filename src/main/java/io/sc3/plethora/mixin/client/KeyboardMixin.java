package io.sc3.plethora.mixin.client;

import io.sc3.plethora.gameplay.modules.keyboard.ClientKeyListener;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
  @Shadow
  @Final
  private MinecraftClient client;

  @Inject(
    method = "onKey",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;"
    )
  )
  private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
    ClientKeyListener.onKeyEvent(key, action);
  }

  @Inject(
    method = "onChar",
    at = @At("HEAD")
  )
  private void onKey(long window, int codePoint, int modifiers, CallbackInfo ci) {
    if (window == client.getWindow().getHandle() && client.currentScreen == null) {
      ClientKeyListener.onCharEvent(codePoint);
    }
  }
}
