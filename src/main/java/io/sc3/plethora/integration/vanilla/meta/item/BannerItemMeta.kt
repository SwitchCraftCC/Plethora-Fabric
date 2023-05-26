package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.block.entity.BannerPattern
import net.minecraft.item.BannerItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.Registries.BANNER_PATTERN
import net.minecraft.util.DyeColor

object BannerItemMeta : ItemStackMetaProvider<BannerItem>(BannerItem::class.java) {
  override fun getMeta(stack: ItemStack, item: BannerItem): Map<String, *> {
    val nbt = stack.getSubNbt("BlockEntityTag")
    val banner = if (nbt != null && nbt.contains("Patterns")) {
      val list = nbt.getList("Patterns", NbtElement.COMPOUND_TYPE.toInt())

      val out = mutableListOf<Map<String, Any>>()
      for (i in 0 until list.size.coerceAtMost(6)) {
        val patternNbt = list.getCompound(i)
        val color = DyeColor.byId(patternNbt.getInt("Color"))
        val pattern = getPatternById(patternNbt.getString("Pattern"))
        if (pattern != null) {
          out.add(mapOf(
            "id" to pattern.id,
            "name" to pattern.id, // TODO: This has changed
            "colour" to color.getName(),
            "color" to color.getName()
          ))
        }
      }
      out
    } else {
      emptyList()
    }

    return mapOf("banner" to banner)
  }

  private fun getPatternById(id: String): BannerPattern? =
    BANNER_PATTERN.find { it.id == id }
}
