package io.sc3.plethora.gameplay.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.IntSupplier
import java.util.function.Supplier

class DynamicImageButton(
  x: Int, y: Int, width: Int, height: Int,
  private val xTexStart: IntSupplier,
  private val yTexStart: Int,
  private val yDiffTex: Int,
  private val texture: Identifier,
  private val textureWidth: Int,
  private val textureHeight: Int,
  onPress: PressAction,
  private val messageSupplier: Supplier<HintedMessage>
) : ButtonWidget(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER) {
  public override fun renderButton(ctx: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
    RenderSystem.enableBlend()
    RenderSystem.enableDepthTest()

    var yTex = yTexStart
    if (isSelected) yTex += yDiffTex

    ctx.drawTexture(
      texture, x, y, xTexStart.asInt.toFloat(), yTex.toFloat(), width, height, textureWidth, textureHeight
    )
  }

  override fun render(ctx: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
    val (newMessage, newTooltip) = messageSupplier.get()
    message = newMessage
    tooltip = newTooltip
    super.render(ctx, mouseX, mouseY, partialTicks)
  }

  @JvmRecord
  data class HintedMessage(val message: Text?, val tooltip: Tooltip) {
  }
}
