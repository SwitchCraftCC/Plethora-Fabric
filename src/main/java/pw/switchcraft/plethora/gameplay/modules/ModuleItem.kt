package pw.switchcraft.plethora.gameplay.modules

import dan200.computercraft.api.client.TransformedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AffineTransformation
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.api.method.IContextBuilder
import pw.switchcraft.plethora.api.module.IModuleAccess
import pw.switchcraft.plethora.api.module.IModuleHandler
import pw.switchcraft.plethora.api.reference.Reference
import pw.switchcraft.plethora.gameplay.BaseItem
import pw.switchcraft.plethora.integration.EntityIdentifier.Player

abstract class ModuleItem(
  itemName: String,
  settings: Settings
) : BaseItem(itemName, settings), IModuleHandler {
  override fun getTranslationKey() = "item." + Plethora.MOD_ID + ".module.module_" + itemName

  // TODO: isBlacklisted
  override fun getModel(delta: Float): TransformedModel {
    return TransformedModel.of(
      this.defaultStack,
      AffineTransformation(null, null, null, null)
    )
  }

  override fun getAdditionalContext(stack: ItemStack, access: IModuleAccess, builder: IContextBuilder) {
    val moduleKey = module.toString()

    val server = access.server
    val entity = ModuleContextHelpers.getEntity(server, stack)
    if (entity != null) builder.addContext(moduleKey, entity, Reference.entity(entity))

    val profile = ModuleContextHelpers.getProfile(stack)
    if (profile != null) builder.addContext(moduleKey, Player(profile))
  }
}
