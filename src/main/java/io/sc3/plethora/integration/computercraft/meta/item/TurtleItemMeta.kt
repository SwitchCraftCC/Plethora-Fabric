package io.sc3.plethora.integration.computercraft.meta.item

import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.shared.turtle.items.ITurtleItem
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

class TurtleItemMeta : ItemStackMetaProvider<ITurtleItem>(ITurtleItem::class.java, "turtle") {
  override fun getMeta(stack: ItemStack, item: ITurtleItem): Map<String, *> {
    val out: MutableMap<String, Any?> = HashMap()

    val colour = item.getColour(stack)
    if (colour != -1) {
      out["color"] = colour
      out["colour"] = colour
    }

    out["fuel"] = item.getFuelLevel(stack)

    out["left"] = getUpgrade(item.getUpgrade(stack, TurtleSide.LEFT))
    out["right"] = getUpgrade(item.getUpgrade(stack, TurtleSide.RIGHT))

    return out
  }

  companion object {
    fun getUpgrade(upgrade: ITurtleUpgrade?): Map<String, String>? {
      if (upgrade == null) return null
      return mapOf(
        "id"        to upgrade.upgradeID.toString(),
        "adjective" to upgrade.unlocalisedAdjective,
        "type"      to upgrade.type.toString()
      )
    }
  }
}
