package io.sc3.plethora.gameplay.client.gui

import dan200.computercraft.client.gui.AbstractComputerScreen
import dan200.computercraft.client.gui.GuiSprites
import dan200.computercraft.client.gui.widgets.ComputerSidebar
import dan200.computercraft.client.gui.widgets.TerminalWidget
import dan200.computercraft.client.render.RenderTypes
import dan200.computercraft.client.render.SpriteRenderer
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu
import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.NEURAL_START_X
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.S
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.START_Y
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.slotPositions
import io.sc3.plethora.gameplay.neural.NeuralInterfaceScreenHandler.Companion.swapBtn
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable

class NeuralInterfaceScreen(
  private val container: NeuralInterfaceScreenHandler,
  playerInv: PlayerInventory,
  title: Text,
) : AbstractComputerScreen<NeuralInterfaceScreenHandler>(
  container, playerInv, translatable("gui.plethora.neuralInterface.title"), BORDER
) {
  private var peripherals = true

  override fun init() {
    backgroundWidth = TEX_WIDTH + AbstractComputerMenu.SIDEBAR_WIDTH
    backgroundHeight = TEX_HEIGHT
    super.init()
  }

  fun initNeural() {
    // Draw the button to swap between peripherals/modules view
    addDrawableChild(DynamicImageButton(
      x + swapBtn.x(), y + swapBtn.y(), 16, 16,
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

  override fun createTerminal() = TerminalWidget(
    terminalData, input,
    x + BORDER + AbstractComputerMenu.SIDEBAR_WIDTH,
    y + BORDER
  )

  override fun drawBackground(ctx: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
    ctx.drawTexture(tex, x + AbstractComputerMenu.SIDEBAR_WIDTH, y, 0, 0, TEX_WIDTH, TEX_HEIGHT)

    // Peripheral direction overlay
    if (peripherals) {
      ctx.drawTexture(tex, x + NEURAL_START_X + 1 + S, y + START_Y + 1, 32, ICON_Y, 16, 16); // Top
      ctx.drawTexture(tex, x + NEURAL_START_X + 1, y + START_Y + 1 + S, 50, ICON_Y, 16 * 3, 16); // Middle 3
      ctx.drawTexture(tex, x + NEURAL_START_X + 1 + S, y + START_Y + 1 + 2 * S, 104, ICON_Y, 16, 16); // Bottom
    }

    val spriteRenderer = SpriteRenderer.createForGui(ctx, RenderTypes.GUI_SPRITES)
    ComputerSidebar.renderBackground(spriteRenderer, computerTextures, x, y + sidebarYOffset)
    ctx.draw()
  }

  private fun updateVisible() {
    setVisible(container.peripheralSlots, peripherals)
    setVisible(container.moduleSlots, !peripherals)
  }

  companion object {
    private val tex = ModId("textures/gui/neural_interface.png")
    private val computerTextures = GuiSprites.getComputerTextures(ComputerFamily.NORMAL)

    private const val ICON_Y = 224

    private const val TEX_SIZE = 256
    private const val TEX_WIDTH = 254
    private const val TEX_HEIGHT = 217

    private val modulesText = translatable("gui.plethora.neuralInterface.modules")
    private val TOOLTIP_MODULES = DynamicImageButton.HintedMessage(modulesText, Tooltip.of(modulesText))

    private val peripheralsText = translatable("gui.plethora.neuralInterface.peripherals")
    private val TOOLTIP_PERIPHERALS = DynamicImageButton.HintedMessage(peripheralsText, Tooltip.of(peripheralsText))

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
