package io.sc3.plethora.gameplay

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import io.sc3.library.Tooltips.addDescLines
import io.sc3.plethora.Plethora.modId

abstract class BaseItem(protected val itemName: String, settings: Settings) : Item(settings) {
  override fun getTranslationKey() = "item.$modId.$itemName"

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    super.appendTooltip(stack, world, tooltip, context)
    addDescLines(tooltip, getTranslationKey(stack))
  }
}
