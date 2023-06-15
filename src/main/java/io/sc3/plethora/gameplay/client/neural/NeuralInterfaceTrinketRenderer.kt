package io.sc3.plethora.gameplay.client.neural

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.client.TrinketRenderer
import io.sc3.plethora.Plethora.ModId
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack

class NeuralInterfaceTrinketRenderer : TrinketRenderer {
  private val tex = ModId("textures/models/neural_interface.png")
  private val model by lazy { NeuralInterfaceModel() }

  override fun render(stack: ItemStack, slotReference: SlotReference, contextModel: EntityModel<out LivingEntity>,
                      matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, entity: LivingEntity,
                      limbAngle: Float, limbDistance: Float, tickDelta: Float, animationProgress: Float, headYaw: Float,
                      headPitch: Float) {
    model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch)
    model.animateModel(entity, limbAngle, limbDistance, tickDelta)
    TrinketRenderer.followBodyRotations(entity, model)

    val consumer = vertexConsumers.getBuffer(model.getLayer(tex))
    model.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f)
  }
}
