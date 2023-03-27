package io.sc3.plethora.gameplay.overlay

import com.mojang.blaze3d.platform.GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
import com.mojang.blaze3d.platform.GlStateManager.DstFactor.ZERO
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor.ONE
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor.SRC_ALPHA
import com.mojang.blaze3d.systems.RenderSystem
import io.sc3.plethora.Plethora.ModId
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Camera
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat.DrawMode.QUADS
import net.minecraft.client.render.VertexFormats.POSITION_TEXTURE
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix4f
import java.awt.Color

open class FlareOverlayRenderer {
  companion object {
    private val tex = ModId("textures/misc/flare.png")

    fun initFlareRenderer(matrices: MatrixStack, camera: Camera) {
      RenderSystem.disableDepthTest()
      RenderSystem.disableCull()
      RenderSystem.enableBlend()
      RenderSystem.blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO)

      matrices.push()

      matrices.translate(-camera.pos.x, -camera.pos.y, -camera.pos.z)

      RenderSystem.setShader(GameRenderer::getPositionTexProgram)
      RenderSystem.setShaderTexture(0, tex)
    }

    fun uninitFlareRenderer(matrices: MatrixStack) {
      matrices.pop()

      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
      RenderSystem.defaultBlendFunc()
      RenderSystem.disableBlend()
      RenderSystem.enableDepthTest()
    }

    fun renderFlare(matrices: MatrixStack, camera: Camera,
                    ticks: Float, x: Double, y: Double, z: Double, color: FlareColor, size: Float) {
      matrices.push()

      // Set up the view
      matrices.translate(x, y, z)
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.yaw))
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.pitch))

      // The size is function of ticks and the id: ensures slightly different sizes
      val renderSize = size * 0.2f + MathHelper.sin(ticks / 100.0f + color.offset) / 16.0f

      // Prepare to render
      val tessellator = Tessellator.getInstance()
      val matrix4f = matrices.peek().positionMatrix

      // Inner highlight
      RenderSystem.setShaderColor(color.r, color.g, color.b, 0.5f)
      renderQuad(tessellator, matrix4f, renderSize)

      // Outer aura
      RenderSystem.setShaderColor(color.r, color.g, color.b, 0.2f)
      renderQuad(tessellator, matrix4f, renderSize * 2)

      matrices.pop()
    }

    private fun renderQuad(tessellator: Tessellator, matrix4f: Matrix4f, size: Float) {
      val buffer = tessellator.buffer

      buffer.begin(QUADS, POSITION_TEXTURE)
      buffer.vertex(matrix4f, -size, -size, 0f).texture(0f, 1f).next()
      buffer.vertex(matrix4f, -size, +size, 0f).texture(1f, 1f).next()
      buffer.vertex(matrix4f, +size, +size, 0f).texture(1f, 0f).next()
      buffer.vertex(matrix4f, +size, -size, 0f).texture(0f, 0f).next()
      BufferRenderer.drawWithGlobalProgram(buffer.end())
    }

    private fun getOffsetFromId(id: Int) =
      (id % (Math.PI * 2)).toFloat() // Generate an offset based off the hash code

    fun getFlareColorById(colourConfig: Map<String, String>, id: Identifier): FlareColor {
      val idString = id.toString()

      // Generate an offset based off the hash code
      val hashCode = id.hashCode()
      val offset = getOffsetFromId(hashCode)

      return if (colourConfig.containsKey(idString)) {
        // Get the colour from the config
        val raw = Color.decode(colourConfig[idString])
        FlareColor(raw.red / 255.0f, raw.green / 255.0f, raw.blue / 255.0f, offset)
      } else {
        // Choose a colour from the hash code
        // this isn't very fancy, but it generally works
        val raw = Color(Color.HSBtoRGB(
          MathHelper.sin(offset) / 2.0f + 0.5f,
          MathHelper.cos(offset) / 2.0f + 0.5f,
          1.0f
        ))

        FlareColor(raw.red / 255.0f, raw.green / 255.0f, raw.blue / 255.0f, offset)
      }
    }
  }

  data class FlareColor(val r: Float, val g: Float, val b: Float, val offset: Float)
}
