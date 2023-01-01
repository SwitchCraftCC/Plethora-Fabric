package io.sc3.plethora.gameplay.registry

import dan200.computercraft.api.detail.DetailProvider
import io.sc3.plethora.Plethora.log
import io.sc3.plethora.core.ContextFactory
import io.sc3.plethora.core.executor.BasicExecutor
import io.sc3.plethora.integration.MetaWrapper
import net.minecraft.item.ItemStack

object ItemDetailsProvider : DetailProvider<ItemStack> {
  // These keys are handled by calling ItemDetails.fill in BasicItemMeta, but continue to ignore them to prevent
  // accidental overwrite if BasicItemMeta changes again.
  private val IGNORE_KEYS = setOf(
    "name", "displayName", "damage", "maxDamage",
    "count", "maxCount", "nbtHash", "durability", "lore",
    "tags", "itemGroups", "enchantments", "unbreakable"
  )

  override fun provideDetails(out: MutableMap<in String, Any?>, stack: ItemStack) {
    // Supply Plethora's item meta to CC items
    try {
      val wrapper = MetaWrapper.of(stack.copy())
      val meta = ContextFactory.of(wrapper).withExecutor(BasicExecutor.INSTANCE).baked.meta
      for ((key, value) in meta) {
        // Don't overwrite CC's data or add our own similarly-named keys. CC gets the final say on what it wants
        // to do with these keys to ensure compatibility.
        if (IGNORE_KEYS.contains(key)) continue
        out[key] = value
      }
    } catch (e: Exception) {
      log.error("Could not supply item meta to CC", e)
    }
  }
}
