package pw.switchcraft.plethora.gameplay.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan200.computercraft.client.gui.widgets.DynamicImageButton;
import dan200.computercraft.shared.util.NonNullSupplier;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.IntSupplier;

public class PlethoraDynamicImageButton extends DynamicImageButton {
    public PlethoraDynamicImageButton(Screen screen, int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier texture, int textureWidth, int textureHeight, PressAction onPress, List<Text> tooltip) {
        super(screen, x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress, tooltip);
    }

    public PlethoraDynamicImageButton(Screen screen, int x, int y, int width, int height, IntSupplier xTexStart, int yTexStart, int yDiffTex, Identifier texture, int textureWidth, int textureHeight, PressAction onPress, NonNullSupplier<List<Text>> tooltip) {
        super(screen, x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress, tooltip);
    }

    @Override
    public void renderButton(@NotNull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        // The transparent module icon does not render properly with the plain old DynamicImageButton
        RenderSystem.enableBlend();
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
    }
}
