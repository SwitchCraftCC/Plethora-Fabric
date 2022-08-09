package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.TEXT_2D
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.TextObject
import pw.switchcraft.plethora.util.ByteBufUtils
import pw.switchcraft.plethora.util.DirtyingProperty
import pw.switchcraft.plethora.util.Vec2d

class Text(
  id: Int,
  parent: Int
) : ColourableObject(id, parent, TEXT_2D), Positionable2d, Scalable, TextObject {
  override var position by DirtyingProperty(Vec2d.ZERO!!)
  override var scale by DirtyingProperty(1f)

  override var lineHeight: Short by DirtyingProperty(9)
  override var dropShadow = false
  override var text by DirtyingProperty("") { _, new, _ -> lines = splitText(new) }

  private var lines = EMPTY_LINES

  override fun writeInitial(buf: PacketByteBuf) {
    super.writeInitial(buf)
    ByteBufUtils.writeVec2d(buf, position)
    buf.writeFloat(scale)
    buf.writeBoolean(dropShadow)
    buf.writeShort(lineHeight.toInt())
    buf.writeString(text)
  }

  override fun readInitial(buf: PacketByteBuf) {
    super.readInitial(buf)
    position = ByteBufUtils.readVec2d(buf)
    scale = buf.readFloat()
    dropShadow = buf.readBoolean()
    lineHeight = buf.readShort()
    text = buf.readString()
    lines = splitText(text)
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    // If the alpha channel doesn't match a 0xFC, then the font renderer
    // will make it opaque. We also early exit here if we're transparent.
    val alpha = colour and 0xFF
    if (alpha == 0) return
    if (alpha and 0xFC == 0) colour = colour or 0x4

    setupFlat()
    RenderSystem.enableTexture()

    val textRenderer = MinecraftClient.getInstance().textRenderer

    matrices.push()
    matrices.translate(position.x, position.y, 0.0)
    matrices.scale(scale, scale, 1f)

    val buffer = Tessellator.getInstance().buffer
    val immediate = VertexConsumerProvider.immediate(buffer)
    val matrix = matrices.peek().positionMatrix

    var y = 0
    for (fullLine in lines) {
      var x = 0
      for (tabSection in fullLine) {
        // We use 0xRRGGBBAA, but the font renderer expects 0xAARRGGBB, so we rotate the bits
        x = textRenderer.draw(
          tabSection, x.toFloat(), y.toFloat(), Integer.rotateRight(colour, 8), dropShadow, matrix, immediate,
          false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE
        )

        // Round the X coordinate to the next tab stop.
        x = x / TAB_WIDTH * TAB_WIDTH + TAB_WIDTH
      }

      y += lineHeight.toInt()
    }

    immediate.draw()

    matrices.pop()
  }

  companion object {
    /**
     * We use a two-dimensional string array to indicate where tabs are.
     * For example, `"Hello\tworld\nFoo\tBar"` would become
     * `{{"Hello", "world"}, {"Foo", "Bar"}}`.
     *
     * This is used in the rendering to simulate tabs.
     */
    private val EMPTY_LINES: List<List<String>> = emptyList()

    /**
     * A tab is 4 spaces and one space is 4 pixels wide -> 1 tab is 4*4 (16) pixels wide.
     * Used during rendering
     */
    private const val TAB_WIDTH = 16

    private val SPLIT_PATTERN = Regex("\r\n|\n|\r")
    private val TAB_PATTERN = Regex("\t")

    private fun splitText(text: String): List<List<String>> {
      val lines = SPLIT_PATTERN.split(text)

      val splitLines: MutableList<MutableList<String>> = MutableList(lines.size) { mutableListOf() }
      val format = StringBuilder()
      for ((i, line) in lines.withIndex()) {
        val tabs = line.split(TAB_PATTERN).toMutableList()
        splitLines[i] = tabs

        for ((j, tab) in tabs.withIndex()) {
          format.append(tab)
          appendFormat(format, format.toString().also { tabs[j] = it })
        }
      }

      return splitLines
    }

    private fun appendFormat(builder: StringBuilder, text: String) {
      builder.setLength(0)

      val l = text.length
      var i = -1

      while (text.indexOf('\u00a7', i + 1).also { i = it } != -1) {
        if (i < l - 1) {
          val c0 = text[i + 1]

          if (isFormatColor(c0)) {
            builder.setLength(0)
            builder.append('\u00a7').append(c0)
          } else if (isFormatSpecial(c0)) {
            builder.append('\u00a7').append(c0)
          }
        }
      }
    }

    private fun isFormatColor(colorChar: Char) =
      (colorChar in '0'..'9' || colorChar >= 'a') && colorChar <= 'f' || colorChar in 'A'..'F'

    /**
     * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
     */
    private fun isFormatSpecial(formatChar: Char) =
      (formatChar in 'k'..'o' || formatChar >= 'K') && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R'
  }
}
