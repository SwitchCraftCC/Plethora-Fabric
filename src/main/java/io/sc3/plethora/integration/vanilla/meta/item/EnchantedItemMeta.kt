package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

class EnchantedItemMeta : BasicMetaProvider<ItemStack>() {
  override fun getMeta(stack: ItemStack): Map<String, Any> {
    val enchants = EnchantmentHelper.get(stack)
    if (enchants.isEmpty()) return emptyMap()

    return mapOf("enchantments" to enchants.mapNotNull { (enchant, level) ->
      mapOf(
        "name"     to Registries.ENCHANTMENT.getId(enchant).toString(),
        "level"    to level,
        "fullName" to enchant.getName(level).string,
      )
    })
  }
}
