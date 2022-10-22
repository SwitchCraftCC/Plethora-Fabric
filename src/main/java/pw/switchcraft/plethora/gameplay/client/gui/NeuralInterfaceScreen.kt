package pw.switchcraft.plethora.gameplay.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import dan200.computercraft.client.gui.ComputerScreenBase
import dan200.computercraft.client.gui.widgets.ComputerSidebar
import dan200.computercraft.client.gui.widgets.WidgetTerminal
import dan200.computercraft.client.render.ComputerBorderRenderer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import pw.switchcraft.plethora.Plethora.ModId
import pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.NEURAL_START_X
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.S
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.START_Y
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.slotPositions
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.swapBtn

class NeuralInterfaceScreen(
  private val container: NeuralInterfaceScreenHandler,
  playerInv: PlayerInventory,
  title: Text
) : ComputerScreenBase<NeuralInterfaceScreenHandler>(
  container, playerInv, translatable("gui.plethora.neuralInterface.title"), BORDER
) {
  private val comp = NeuralComputerHandler.getClient(container.stack)
  private var peripherals = true

  override fun init() {
    backgroundWidth = TEX_WIDTH + ComputerSidebar.WIDTH
    backgroundHeight = TEX_HEIGHT
    super.init()
  }

  fun initNeural() {
    // Draw the button to swap between peripherals/modules view
    addDrawableChild(PlethoraDynamicImageButton(
      this, x + swapBtn.x(), y + swapBtn.y(), 16, 16,
      { if (peripherals) 0 else 16 },  // Show the appropriate icon based on the current view
      ICON_Y, 0, tex, TEX_SIZE, TEX_SIZE,
      { // Swap view on click
        peripherals = !peripherals
        updateVisible()
      }
    ) {
      // Show the correct tooltip for the view
      if (peripherals) TOOLTIP_MODULES else TOOLTIP_PERIPHERALS
    })

    updateVisible() // Make one set of peripheral/module slots visible
  }

  override fun createTerminal() = WidgetTerminal(
    comp,
    x + BORDER + ComputerSidebar.WIDTH,
    y + BORDER,
    NeuralComputerHandler.WIDTH, NeuralComputerHandler.HEIGHT
  )

  override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, tex)

    drawTexture(matrices, x + ComputerSidebar.WIDTH, y, 0, 0, TEX_WIDTH, TEX_HEIGHT)

    // Peripheral direction overlay
    if (peripherals) {
      drawTexture(matrices, x + NEURAL_START_X + 1 + S, y + START_Y + 1, 32, ICON_Y, 16, 16); // Top
      drawTexture(matrices, x + NEURAL_START_X + 1, y + START_Y + 1 + S, 50, ICON_Y, 16 * 3, 16); // Middle 3
      drawTexture(matrices, x + NEURAL_START_X + 1 + S, y + START_Y + 1 + 2 * S, 104, ICON_Y, 16, 16); // Bottom
    }

    RenderSystem.setShaderTexture(0, ComputerBorderRenderer.BACKGROUND_NORMAL)
    ComputerSidebar.renderBackground(matrices, x, y + sidebarYOffset)
  }

  private fun updateVisible() {
    setVisible(container.peripheralSlots, peripherals)
    setVisible(container.moduleSlots, !peripherals)
  }

  companion object {
    private val tex = ModId("textures/gui/neural_interface.png")
    private const val ICON_Y = 224

    private const val TEX_SIZE = 256
    private const val TEX_WIDTH = 254
    private const val TEX_HEIGHT = 217

    private val TOOLTIP_MODULES = listOf(translatable("gui.plethora.neuralInterface.modules"))
    private val TOOLTIP_PERIPHERALS = listOf(translatable("gui.plethora.neuralInterface.peripherals"))

    const val BORDER = 8

    private fun setVisible(slots: List<Slot>, visible: Boolean) {
      slots.forEachIndexed { i, slot ->
        if (visible) {
          val pos = slotPositions[i]
          slot.x = pos.x()
          slot.y = pos.y()
        } else {
          slot.x = -20000
          slot.y = -20000
        }
      }
    }
  }
}
