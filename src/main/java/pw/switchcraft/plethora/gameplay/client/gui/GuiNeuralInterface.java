package pw.switchcraft.plethora.gameplay.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan200.computercraft.client.gui.ComputerScreenBase;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.WidgetTerminal;
import dan200.computercraft.client.render.ComputerBorderRenderer;
import dan200.computercraft.shared.computer.core.ClientComputer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler;
import pw.switchcraft.plethora.util.Vec2i;

import java.util.List;

import static pw.switchcraft.plethora.Plethora.MOD_ID;
import static pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler.HEIGHT;
import static pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler.WIDTH;
import static pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler.*;

public class GuiNeuralInterface extends ComputerScreenBase<NeuralInterfaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/gui/neural_interface.png");
    private static final int ICON_Y = 224;

    private static final int TEX_SIZE = 256;
    private static final int TEX_WIDTH = 254;
    private static final int TEX_HEIGHT = 217;

    private static final List<Text> TOOLTIP_MODULES =
        List.of(new TranslatableText("gui.plethora.neuralInterface.modules"));
    private static final List<Text> TOOLTIP_PERIPHERALS =
        List.of(new TranslatableText("gui.plethora.neuralInterface.peripherals"));

    public static final int BORDER = 8;

    private final NeuralInterfaceScreenHandler container;
    private final ClientComputer computer;

    private boolean peripherals = true;

    public GuiNeuralInterface(NeuralInterfaceScreenHandler container, PlayerInventory player, Text title) {
        super(container, player, new TranslatableText("gui.plethora.neuralInterface.title"), BORDER);

        this.container = container;
        computer = NeuralComputerHandler.getClient(container.getStack());

        backgroundWidth = TEX_WIDTH + ComputerSidebar.WIDTH;
        backgroundHeight = TEX_HEIGHT;
    }

    public void initNeural() {
        // Draw the button to swap between peripherals/modules view
        addDrawableChild(new PlethoraDynamicImageButton(
            this, x + SWAP.x(), y + SWAP.y(), 16, 16,
            () -> peripherals ? 0 : 16, // Show the appropriate icon based on the current view
            ICON_Y, 0, TEXTURE, TEX_SIZE, TEX_SIZE,
            b -> { // Swap view on click
                peripherals = !peripherals;
                updateVisible();
            },
            () -> peripherals // Show the correct tooltip for the view
                ? TOOLTIP_MODULES
                : TOOLTIP_PERIPHERALS
        ));

        updateVisible(); // Make one set of peripheral/module slots visible
    }

    @Override
    protected WidgetTerminal createTerminal() {
        return new WidgetTerminal(
            computer,
            x + BORDER + ComputerSidebar.WIDTH,
            y + BORDER,
            WIDTH, HEIGHT
        );
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        drawTexture(matrices, x + ComputerSidebar.WIDTH, y, 0, 0, TEX_WIDTH, TEX_HEIGHT);

        // Peripheral direction overlay
        if (peripherals) {
            drawTexture(matrices, x + NEURAL_START_X + 1 + S, y + START_Y + 1, 32, ICON_Y, 16, 16); // Top
            drawTexture(matrices, x + NEURAL_START_X + 1, y + START_Y + 1 + S, 50, ICON_Y, 16 * 3, 16); // Middle 3
            drawTexture(matrices, x + NEURAL_START_X + 1 + S, y + START_Y + 1 + 2 * S, 104, ICON_Y, 16, 16); // Bottom
        }

        RenderSystem.setShaderTexture(0, ComputerBorderRenderer.BACKGROUND_NORMAL);
        ComputerSidebar.renderBackground(matrices, x, y + sidebarYOffset);
    }

    private void updateVisible() {
        setVisible(container.peripheralSlots, peripherals);
        setVisible(container.moduleSlots, !peripherals);
    }

    private static void setVisible(Slot[] slots, boolean visible) {
        for (int i = 0, peripheralSlotsLength = slots.length; i < peripheralSlotsLength; i++) {
            Slot slot = slots[i];
            if (visible) {
                Vec2i pos = NeuralInterfaceScreenHandler.POSITIONS[i];

                // Had to use an access widener for this as slot position is now final - hopefully this is safe!
                slot.x = pos.x();
                slot.y = pos.y();
            } else {
                slot.x = -20000;
                slot.y = -20000;
            }
        }
    }

}
