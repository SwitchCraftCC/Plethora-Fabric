package io.sc3.plethora.gameplay.modules

import dan200.computercraft.api.client.TransformedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AffineTransformation
import io.sc3.plethora.Plethora.modId
import io.sc3.plethora.api.method.IContextBuilder
import io.sc3.plethora.api.module.IModuleAccess
import io.sc3.plethora.api.module.IModuleHandler
import io.sc3.plethora.api.reference.Reference
import io.sc3.plethora.gameplay.BaseItem
import io.sc3.plethora.integration.EntityIdentifier.Player

abstract class ModuleItem(
  itemName: String,
  settings: Settings
) : BaseItem(itemName, settings), IModuleHandler {
  override fun getTranslationKey() = "item.$modId.module.module_$itemName"

  // TODO: isBlacklisted
  override fun getModel(): TransformedModel =
    TransformedModel.of(defaultStack, transform)

  override fun getAdditionalContext(stack: ItemStack, access: IModuleAccess, builder: IContextBuilder) {
    val moduleKey = module.toString()

    val server = access.server
    val entity = ModuleContextHelpers.getEntity(server, stack)
    if (entity != null) builder.addContext(moduleKey, entity, Reference.entity(entity))

    val profile = ModuleContextHelpers.getProfile(stack)
    if (profile != null) builder.addContext(moduleKey, Player(profile))
  }

  companion object {
    private val transform = AffineTransformation(null, null, null, null)
  }
}
