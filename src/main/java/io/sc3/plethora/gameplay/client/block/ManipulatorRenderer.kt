package io.sc3.plethora.gameplay.client.block

import io.sc3.plethora.api.module.IModuleHandler
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlock
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlockEntity
import io.sc3.plethora.util.MatrixHelpers
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

class ManipulatorRenderer : BlockEntityRenderer<ManipulatorBlockEntity> {
  private val mc by lazy { MinecraftClient.getInstance() }
  private val itemRenderer by mc::itemRenderer
  private val missingModel by lazy { mc.bakedModelManager.missingModel }

  override fun render(manipulator: ManipulatorBlockEntity, tickDelta: Float, matrices: MatrixStack,
                      vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
    matrices.push()

    matrices.multiplyPositionMatrix(MatrixHelpers.matrixFor(manipulator.facing))
    matrices.translate(0.0, ManipulatorBlock.OFFSET, 0.0)

    val type = manipulator.manipulatorType
    val rotation = manipulator.incrementRotation(tickDelta)

    val size = type.size()
    val boxes = type.boxesFor(Direction.DOWN)
    for (i in 0 until size) {
      val stack = manipulator.getStack(i)
      if (stack.isEmpty) continue
      val item = stack.item

      val box = boxes[i]

      matrices.push()
      matrices.translate(
        (box.minX + box.maxX) / 2.0f,
        type.scale.toDouble(),
        1 - (box.minZ + box.maxZ) / 2.0f
      )

      // Animated rotation
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((rotation - 90) % 360))

      val model = if (item is IModuleHandler) {
        item.model.model
      } else {
        missingModel
      }

      matrices.scale(type.scale, type.scale, type.scale)
      matrices.translate(0.0, -0.2, 0.0) // ItemRenderer already translates by -0.5f, -0.5f, -0.5f

      // Be sure to use the NONE mode here, GROUND and other modes imply a default translation and scale
      itemRenderer.renderItem(stack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light,
        overlay, model)

      matrices.pop()
    }

    matrices.pop()
  }

  override fun getRenderDistance() = 32
}
