package pw.switchcraft.plethora.gameplay.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan200.computercraft.client.gui.widgets.DynamicImageButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class PlethoraDynamicImageButton extends DynamicImageButton {
    public PlethoraDynamicImageButton(Screen screen, int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier texture, int textureWidth, int textureHeight, PressAction onPress, List<Text> tooltip) {
        super(screen, x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress, tooltip);
    }

    public PlethoraDynamicImageButton(Screen screen, int x, int y, int width, int height, IntSupplier xTexStart, int yTexStart, int yDiffTex, Identifier texture, int textureWidth, int textureHeight, PressAction onPress, Supplier<List<Text>> tooltip) {
        super(screen, x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress, tooltip);
    }

    @Override
    public void renderButton(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        // The transparent module icon does not render properly with the plain old DynamicImageButton
        RenderSystem.enableBlend();
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
    }
}
