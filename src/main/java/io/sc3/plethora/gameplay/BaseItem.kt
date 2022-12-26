package pw.switchcraft.plethora.gameplay

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import pw.switchcraft.library.Tooltips.addDescLines
import pw.switchcraft.plethora.Plethora.modId

abstract class BaseItem(protected val itemName: String, settings: Settings) : Item(settings) {
  override fun getTranslationKey() = "item.$modId.$itemName"

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    super.appendTooltip(stack, world, tooltip, context)
    addDescLines(tooltip, getTranslationKey(stack))
  }
}
