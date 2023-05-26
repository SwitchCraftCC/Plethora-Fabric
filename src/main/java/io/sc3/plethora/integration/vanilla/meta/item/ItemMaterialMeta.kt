package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterials.*

object ItemMaterialMeta : BasicMetaProvider<ItemStack>() {
  override fun getMeta(target: ItemStack): Map<String, *> {
    val item = target.item
    val name = if (item is ToolItem) {
      when (item.material) {
        WOOD      -> "wood"
        STONE     -> "stone"
        IRON      -> "iron"
        GOLD      -> "gold"
        DIAMOND   -> "diamond"
        NETHERITE -> "netherite"
        else      -> "unknown"
      }
    } else {
      (item as? ArmorItem)?.material?.name
    }

    return if (name != null) {
      mapOf("material" to name)
    } else {
      emptyMap<String, Any>()
    }
  }
}
