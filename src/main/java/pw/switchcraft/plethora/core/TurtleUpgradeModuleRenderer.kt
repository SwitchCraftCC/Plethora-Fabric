package pw.switchcraft.plethora.core

import dan200.computercraft.api.client.TransformedModel
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.AffineTransformation
import net.minecraft.util.math.Vec3f
import pw.switchcraft.plethora.api.module.IModuleHandler

object TurtleUpgradeModuleRenderer {
  private val leftTransform = getMatrixFor(-0.40625)
  private val rightTransform = getMatrixFor(0.40625)

  private fun getMatrixFor(offset: Double): AffineTransformation {
    val matrices = MatrixStack()
    matrices.push()

    matrices.translate(0.5, 0.5, 0.5)
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90f))
    matrices.scale(0.8f, 0.8f, 0.8f)
    matrices.translate(-0.5, -0.5, -0.5)
    matrices.translate(-0.025 / 0.8, 0.1 / 0.8, offset / 0.8)

    return AffineTransformation(matrices.peek().positionMatrix)
  }

  @JvmStatic
  fun getModel(handler: IModuleHandler, side: TurtleSide): TransformedModel {
    val model = handler.model

    val baseTransform = if (side == TurtleSide.LEFT) leftTransform else rightTransform
    val transform = baseTransform.multiply(model.matrix)

    return TransformedModel(model.model, transform)
  }
}
