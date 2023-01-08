package io.sc3.plethora.gameplay.modules.keyboard

import dan200.computercraft.client.gui.NoTermComputerScreen
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.MultilineText
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable

class KeyboardComputerScreen<T : AbstractComputerMenu>(
  private val screen: T,
  player: PlayerInventory,
  title: Text
) : NoTermComputerScreen<T>(screen, player, title) {
  private val tr by lazy { MinecraftClient.getInstance().textRenderer }

  private var lines: MultilineText = MultilineText.EMPTY

  override fun init() {
    super.init()
    lines = MultilineText.create(tr, translatable("item.plethora.module.module_keyboard.close"), (width * 0.8).toInt())
  }

  override fun getScreenHandler(): T = screen

  override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
    // Don't call super.render (will render the pocket computer overlay text)
    // This means that Screen.drawables don't get drawn, but since we have none anyway, that's fine
    lines.drawCenterWithShadow(matrices, width / 2, 10, textRenderer.fontHeight, 0xFFFFFF)
  }
}
