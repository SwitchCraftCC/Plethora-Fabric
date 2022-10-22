package pw.switchcraft.plethora.gameplay.client.entity

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import pw.switchcraft.plethora.Plethora.ModId
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

private const val SEGMENTS = 5
private const val TENTACLES = 6
private const val BASE_ANGLE = 25

// Dimensions of the one tentacle
private const val LENGTH = 0.3f
private const val WIDTH = 0.15f

private const val EASING_TICKS = 5.0
private const val OFFSET_SPEED = 0.1
private const val OFFSET_VARIANCE = 7.0

private val SQUID_UUID = UUID.fromString("d3156e4b-c712-4fd3-87b0-b24b8ca94209")

class SquidFeatureRenderer(
  ctx: FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
) : FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(ctx) {
  private val layer = RenderLayer.getEntitySolid(ModId("textures/misc/white.png"))

  private val lastAngles = DoubleArray(TENTACLES * SEGMENTS)
  private val offsets = DoubleArray(TENTACLES * SEGMENTS)

  private var tick = 0.0

  init {
    for (i in lastAngles.indices) {
      lastAngles[i] = BASE_ANGLE.toDouble()
      offsets[i] = Math.random() * Math.PI * 2
    }
  }

  override fun render(matrices: MatrixStack, consumers: VertexConsumerProvider, light: Int,
                      player: AbstractClientPlayerEntity, limbAngle: Float, limbDistance: Float, tickDelta: Float,
                      animationProgress: Float, headYaw: Float, headPitch: Float) {
    val profile = player.gameProfile ?: return
    if (profile.id != SQUID_UUID) return // TODO: config for fun render

    matrices.push()
    if (player.isInSneakingPose) {
      matrices.translate(0.0, 0.2, 0.0)
      matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f / Math.PI.toFloat()))
    }

    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f))
    matrices.translate(0.0, 0.1, -0.3)

    val angle = if (player.hurtTime > 0) {
      val progress = player.hurtTime.toDouble() / player.maxHurtTime.toDouble()
      BASE_ANGLE - (progress * (BASE_ANGLE - OFFSET_VARIANCE))
    } else {
      val velocity = Vec3d(player.lastRenderX, player.lastRenderY, player.lastRenderZ)
        .distanceTo(player.pos)
      val adjusted = 1 - exp(velocity * -2)
      BASE_ANGLE - adjusted * BASE_ANGLE
    }

    tick = (tick + tickDelta) % (Math.PI * 2 / OFFSET_SPEED)

    val consumer = consumers.getBuffer(layer)

    for (i in 0 until TENTACLES) {
      matrices.push()

      matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(360.0f / TENTACLES * i))
      matrices.translate(0.1, 0.0, 0.0)

      for (j in 0 until SEGMENTS) {
        // Offset each tentacle by a random amount
        val lastAngle = lastAngles[i * SEGMENTS + j]
        var thisAngle = angle + sin(offsets[i * SEGMENTS + j] + tick * OFFSET_SPEED) * OFFSET_VARIANCE

        // Angle each tentacle to get a "claw" effect
        thisAngle *= cos(j * (Math.PI / (SEGMENTS - 1)))

        // Provide some basic easing on the angle
        // Basically the middle segments have a high "delta"
        if (abs(lastAngle - thisAngle) > 1) {
          thisAngle = lastAngle - (lastAngle - thisAngle) / EASING_TICKS
        }

        lastAngles[i * SEGMENTS + j] = thisAngle

        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(thisAngle.toFloat()))

        val matrix4f = matrices.peek().positionMatrix
        tentacle(consumer, matrix4f, light)

        matrices.translate(0.0, LENGTH - WIDTH / 2.0, 0.0)
      }

      matrices.pop()
    }

    matrices.pop()
  }

  private fun tentacle(consumer: VertexConsumer, matrix4f: Matrix4f, light: Int) {
    vertex(consumer, matrix4f, 0.0f, 0.0f, -WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, 0.0f, WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, LENGTH, WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, LENGTH, -WIDTH / 2, light)

    vertex(consumer, matrix4f, WIDTH, 0.0f, -WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, 0.0f, WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, 0.0f, WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, 0.0f, -WIDTH / 2, light)

    vertex(consumer, matrix4f, 0.0f, 0.0f, -WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, LENGTH, -WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, LENGTH, -WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, 0.0f, -WIDTH / 2, light)

    vertex(consumer, matrix4f, WIDTH, LENGTH, -WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, LENGTH, WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, 0.0f, WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, 0.0f, -WIDTH / 2, light)

    vertex(consumer, matrix4f, 0.0f, LENGTH, -WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, LENGTH, WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, LENGTH, WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, LENGTH, -WIDTH / 2, light)

    vertex(consumer, matrix4f, WIDTH, 0.0f, WIDTH / 2, light)
    vertex(consumer, matrix4f, WIDTH, LENGTH, WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, LENGTH, WIDTH / 2, light)
    vertex(consumer, matrix4f, 0.0f, 0.0f, WIDTH / 2, light)
  }

  private fun vertex(consumer: VertexConsumer, matrix4f: Matrix4f, x: Float, y: Float, z: Float,
                     light: Int, color: Float = 0.0f) {
    consumer // POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
      .vertex(matrix4f, x, y, z)
      .color(color, 0.0f, 0.0f, 1.0f)
      .texture(0.0f, 0.0f)
      .overlay(OverlayTexture.DEFAULT_UV)
      .light(light)
      .normal(0.0f, 0.0f, 1.0f)
      .next()
  }
}
