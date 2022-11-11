package pw.switchcraft.plethora.gameplay.registry

import dan200.computercraft.api.detail.IDetailProvider
import net.minecraft.item.ItemStack
import pw.switchcraft.plethora.Plethora.log
import pw.switchcraft.plethora.core.ContextFactory
import pw.switchcraft.plethora.core.executor.BasicExecutor
import pw.switchcraft.plethora.integration.MetaWrapper

object ItemDetailsProvider : IDetailProvider<ItemStack> {
  // TODO: Probably better eventually to replace BasicItemMeta with ItemData.fill
  private val IGNORE_KEYS = setOf(
    "name", "displayName", "damage", "maxDamage",
    "count", "maxCount", "nbtHash", "durability", "lore"
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
