package io.sc3.plethora.gameplay.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan200.computercraft.client.gui.widgets.DynamicImageButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class PlethoraDynamicImageButton extends DynamicImageButton {
  public PlethoraDynamicImageButton(
    int x, int y, int width, int height, IntSupplier xTexStart, int yTexStart, int yDiffTex,
    Identifier texture, int textureWidth, int textureHeight,
    PressAction onPress, Supplier<HintedMessage> message
  ) {
    super(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress, message);
  }

  @Override
  public void renderButton(@Nonnull DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
    // The transparent module icon does not render properly with the plain old DynamicImageButton
    RenderSystem.enableBlend();
    super.renderButton(ctx, mouseX, mouseY, partialTicks);
    RenderSystem.disableBlend();
  }
}
