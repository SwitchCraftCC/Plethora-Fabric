package io.sc3.plethora.integration.vanilla.meta.item

import dan200.computercraft.shared.util.NBTUtil
import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

class BasicItemMeta : BasicMetaProvider<ItemStack>() {
  override fun getMeta(stack: ItemStack): Map<String, *> {
    if (stack.isEmpty) return emptyMap<String, Any>()

    val data = HashMap<String, Any?>()
    fillBasicMeta(data, stack)

    val displayText = stack.name
    val display = displayText?.string

    data["displayName"] = if (display.isNullOrEmpty()) stack.translationKey else display
    data["rawName"] = stack.translationKey

    data["maxCount"] = stack.maxCount
    data["maxDamage"] = stack.maxDamage

    if (stack.item.isItemBarVisible(stack)) {
      data["durability"] = stack.item.getItemBarStep(stack)
    }

    return data
  }

  companion object {
    fun getBasicMeta(stack: ItemStack): HashMap<String, Any?> {
      val data = HashMap<String, Any?>()
      fillBasicMeta(data, stack)
      return data
    }

    fun fillBasicMeta(data: HashMap<String, Any?>, stack: ItemStack) {
      data["name"] = Registries.ITEM.getId(stack.item).toString()
      data["damage"] = stack.damage
      data["count"] = stack.count
      data["nbt"] = getNbtHash(stack)
    }

    private fun getNbtHash(stack: ItemStack): String? =
      if (stack.hasNbt()) NBTUtil.getNBTHash(stack.nbt) else null
  }
}
