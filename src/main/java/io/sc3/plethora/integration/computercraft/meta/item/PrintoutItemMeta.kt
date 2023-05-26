package io.sc3.plethora.integration.computercraft.meta.item

import dan200.computercraft.shared.media.items.PrintoutItem
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

class PrintoutItemMeta : ItemStackMetaProvider<PrintoutItem>(PrintoutItem::class.java, "printout") {
  override fun getMeta(stack: ItemStack, item: PrintoutItem): Map<String, *> {
    val lines: MutableMap<Int, String> = HashMap()
    val lineArray = PrintoutItem.getText(stack)
    for (i in lineArray.indices) {
      lines[i + 1] = lineArray[i]
    }

    return mapOf(
      "type"  to item.type.toString(),
      "title" to PrintoutItem.getTitle(stack),
      "pages" to PrintoutItem.getPageCount(stack),
      "lines" to lines
    )
  }
}
