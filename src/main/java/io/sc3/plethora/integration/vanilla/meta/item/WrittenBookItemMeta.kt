package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack
import net.minecraft.item.WrittenBookItem
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text

object WrittenBookItemMeta : ItemStackMetaProvider<WrittenBookItem>(WrittenBookItem::class.java) {
  override fun getMeta(stack: ItemStack, item: WrittenBookItem): Map<String, *> {
    val nbt = stack.nbt ?: return emptyMap<String, Any>()

    return mapOf(
      "generation" to WrittenBookItem.getGeneration(stack),
      "author" to nbt.getString(WrittenBookItem.AUTHOR_KEY),
      "pages" to nbt.getList(WrittenBookItem.PAGES_KEY, NbtCompound.STRING_TYPE.toInt())
        ?.filterIsInstance<NbtString>()
        ?.map { Text.Serializer.fromJson(it.asString())?.string },
      "opened" to nbt.getBoolean(WrittenBookItem.RESOLVED_KEY)
    )
  }
}
