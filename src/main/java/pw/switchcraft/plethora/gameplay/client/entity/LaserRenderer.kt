package pw.switchcraft.plethora.gameplay.client.entity

import net.minecraft.client.render.*
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix4f
import pw.switchcraft.plethora.Plethora.ModId
import pw.switchcraft.plethora.gameplay.modules.laser.LaserEntity

private const val SCALE = 0.05625f

class LaserRenderer(ctx: EntityRendererFactory.Context?) : EntityRenderer<LaserEntity>(ctx) {
  private val layer = RenderLayer.getEntityTranslucent(ModId("textures/misc/white.png"))

  override fun render(entity: LaserEntity, yaw: Float, tickDelta: Float, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int) {
    matrices.push()

    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.prevYaw + (entity.yaw - entity.prevYaw) * tickDelta - 90.0f))
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.prevPitch + (entity.pitch - entity.prevPitch) * tickDelta))

    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0f))
    matrices.scale(SCALE, SCALE, SCALE)
    matrices.translate(-4.0, 0.0, 0.0)

    val consumer = vertexConsumers.getBuffer(layer)
    val matrix4f = matrices.peek().positionMatrix

    for (i in 0 until 2) {
      matrix4f.rotateX((Math.PI / 2).toFloat())

      vertex(consumer, matrix4f, -9f, -2f, 0f, 0f, 1f, 0.0f)
      vertex(consumer, matrix4f, +9f, -2f, 0f, 1f, 1f, 0.0f)
      vertex(consumer, matrix4f, +9f, -1.5f, 0f, 1f, 0f, 0.25f)
      vertex(consumer, matrix4f, -9f, -1.5f, 0f, 0f, 0f, 0.25f)

      vertex(consumer, matrix4f, -9f, -1.5f, 0f, 0f, 1f, 0.25f)
      vertex(consumer, matrix4f, +9f, -1.5f, 0f, 1f, 1f, 0.25f)
      vertex(consumer, matrix4f, +9f, +0f, 0f, 1f, 0f, 0.8f)
      vertex(consumer, matrix4f, -9f, +0f, 0f, 0f, 1f, 0.8f)

      vertex(consumer, matrix4f, -9f, +0f, 0f, 0f, 1f, 0.8f)
      vertex(consumer, matrix4f, +9f, +0f, 0f, 1f, 1f, 0.8f)
      vertex(consumer, matrix4f, +9f, +1.5f, 0f, 1f, 0f, 0.25f)
      vertex(consumer, matrix4f, -9f, +1.5f, 0f, 0f, 0f, 0.25f)

      vertex(consumer, matrix4f, -9f, +1.5f, 0f, 0f, 1f, 0.25f)
      vertex(consumer, matrix4f, +9f, +1.5f, 0f, 1f, 1f, 0.25f)
      vertex(consumer, matrix4f, +9f, +2f, 0f, 1f, 0f, 0.0f)
      vertex(consumer, matrix4f, -9f, +2f, 0f, 0f, 0f, 0.0f)
    }

    matrices.pop()
  }

  override fun getTexture(entity: LaserEntity): Identifier? = null

  private fun vertex(consumer: VertexConsumer, matrix4f: Matrix4f, x: Float, y: Float, z: Float,
                     u: Float, v: Float, alpha: Float) {
    consumer
      .vertex(matrix4f, x, y, z)
      .color(1f, 0f, 0f, alpha)
      .texture(u, v)
      .overlay(OverlayTexture.DEFAULT_UV)
      .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
      .normal(0.0f, 0.0f, 1.0f)
      .next()
  }
}
