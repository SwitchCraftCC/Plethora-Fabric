package io.sc3.plethora.gameplay.client.neural

import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPartBuilder.create
import net.minecraft.client.model.ModelTransform.pivot
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames.HEAD
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier

class NeuralInterfaceModel(
  renderLayerFactory: (Identifier) -> RenderLayer = RenderLayer::getEntityCutout,
) : BipedEntityModel<LivingEntity>(createModelData().createModel(), renderLayerFactory) {
  init {
    setVisible(false)
    head.visible = true
  }

  companion object {
    private fun createModelData(): TexturedModelData {
      val modelData = getModelData(Dilation.NONE, 0.0f)
      val root = modelData.root

      val main = root.addChild(
        HEAD,
        create().uv(0, 0).cuboid(-0.1f, -5.0f, -5.1f, 5.0f, 3.0f, 1.0f),
        pivot(0.0f, 0.0f, 0.0f)
      )

      main.addChild(
        "neural_normal_side",
        create().uv(5, 0).cuboid(3.9f, -5.0f, -4.1f, 1.0f, 2.0f, 7.0f),
        pivot(0.0f, 0.0f, 0.0f)
      )

      return TexturedModelData.of(modelData, 22, 9)
    }
  }
}
