package io.sc3.plethora.integration.computercraft.meta.item

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack

class PocketComputerItemMeta : ItemStackMetaProvider<PocketComputerItem>(PocketComputerItem::class.java, "pocket") {
  override fun getMeta(stack: ItemStack, item: PocketComputerItem): Map<String, *> {
    val out: MutableMap<String, Any?> = HashMap(2)

    val colour = item.getColour(stack)
    if (colour != -1) {
      out["color"] = colour
      out["colour"] = colour
    }

    out["back"] = getUpgrade(PocketComputerItem.getUpgrade(stack))

    return out
  }

  companion object {
    private fun getUpgrade(upgrade: IPocketUpgrade?): Map<String, String>? {
      if (upgrade == null) return null
      return mapOf(
        "id"        to upgrade.upgradeID.toString(),
        "adjective" to upgrade.unlocalisedAdjective
      )
    }
  }
}
