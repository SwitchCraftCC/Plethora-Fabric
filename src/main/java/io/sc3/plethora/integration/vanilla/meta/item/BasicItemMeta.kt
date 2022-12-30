package io.sc3.plethora.integration.vanilla.meta.item

import dan200.computercraft.shared.util.NBTUtil
import io.sc3.plethora.Plethora
import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.Registries
import java.util.stream.Collectors

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

    val nbt = stack.nbt
    if (nbt != null && nbt.contains("display", NbtElement.COMPOUND_TYPE.toInt())) {
      val displayNbt = nbt.getCompound("display")
      if (displayNbt.contains("Lore", NbtElement.LIST_TYPE.toInt())) {
        val loreNbt = displayNbt.getList("Lore", NbtElement.STRING_TYPE.toInt())
        data["lore"] = loreNbt.stream().map(NbtElement::asString).collect(Collectors.toList())
      }
    }

    val tags = mutableSetOf<String>()
    try {
      // Add all the item tags
      stack.streamTags().forEach { t -> tags.add(t.id().toString()) }

      // If this is a block item, add the block tags too. Default implementation of getBlockFromItem is to check if
      // the item is an instance of BlockItem, but a few blocks are known to have their own item classes and will
      // override this.
      val block = Block.getBlockFromItem(stack.item)
      if (block !== Blocks.AIR) {
        Registries.BLOCK.getEntry(block).streamTags().forEach { t -> tags.add(t.id().toString()) }
      }
    } catch (e: Exception) {
      Plethora.log.error("Failed to get tags for item {}", stack, e)
    }
    data["tags"] = tags

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
