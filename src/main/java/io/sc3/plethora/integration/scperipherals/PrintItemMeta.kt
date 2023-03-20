package io.sc3.plethora.integration.scperipherals

import io.sc3.peripherals.prints.PrintItem
import io.sc3.peripherals.prints.Shapes
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

class PrintItemMeta : ItemStackMetaProvider<PrintItem>(PrintItem::class.java) {
  override fun getMeta(stack: ItemStack, item: PrintItem): Map<String, *> {
    val print = PrintItem.printData(stack) ?: return emptyMap<String, Any?>()
    return mapOf(
      "tooltip"        to print.tooltip, // Or `null`
      "isButton"       to print.isButton,
      "collideWhenOn"  to print.collideWhenOn,
      "collideWhenOff" to print.collideWhenOff,
      "isBeaconBlock"  to print.isBeaconBlock,
      "redstoneLevel"  to print.redstoneLevel,
      "lightLevel"     to print.lightLevel,
      "shapesOff"      to print.shapesOff.toMap(),
      "shapesOn"       to print.shapesOn.toMap()
    )
  }

  private fun Shapes.toMap() = mapOf(
    // TODO: Currently the shapes themselves are not exposed here to prevent print stealing.
    "totalVolume" to totalVolume,
    "totalSurfaceArea" to totalSurfaceArea
  )
}
