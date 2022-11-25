package pw.switchcraft.plethora.core

import dan200.computercraft.api.client.TransformedModel
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.AffineTransformation
import net.minecraft.util.math.RotationAxis

object TurtleUpgradeModuleRenderer : TurtleUpgradeModeller<TurtleUpgradeModule> {
  private val leftTransform = getMatrixFor(-0.40625)
  private val rightTransform = getMatrixFor(0.40625)

  private fun getMatrixFor(offset: Double): AffineTransformation {
    val matrices = MatrixStack()
    matrices.push()

    matrices.translate(0.5, 0.5, 0.5)
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90f))
    matrices.scale(0.8f, 0.8f, 0.8f)
    matrices.translate(-0.5, -0.5, -0.5)
    matrices.translate(-0.025 / 0.8, 0.1 / 0.8, offset / 0.8)

    return AffineTransformation(matrices.peek().positionMatrix)
  }

  override fun getModel(module: TurtleUpgradeModule, turtle: ITurtleAccess?, side: TurtleSide): TransformedModel {
    val model = module.handler.model

    val baseTransform = if (side == TurtleSide.LEFT) leftTransform else rightTransform
    val transform = baseTransform.multiply(model.matrix)

    return TransformedModel(model.model, transform)
  }
}
