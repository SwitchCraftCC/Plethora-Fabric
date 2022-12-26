package io.sc3.plethora.api.module

import dan200.computercraft.api.client.TransformedModel
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.math.AffineTransformation

/**
 * A basic module handler.
 */
open class BasicModuleHandler(
  private val id: Identifier,
  private val item: Item
) : AbstractModuleHandler() {
  override fun getModule() = id
  override fun getModel(): TransformedModel = TransformedModel.of(item.defaultStack, transform)

  companion object {
    val transform = AffineTransformation(null, null, null, null)
  }
}
