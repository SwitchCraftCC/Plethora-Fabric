package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.ItemStack
import net.minecraft.item.LingeringPotionItem
import net.minecraft.item.PotionItem
import net.minecraft.item.SplashPotionItem
import net.minecraft.potion.PotionUtil
import net.minecraft.registry.Registries

object PotionItemMeta : ItemStackMetaProvider<PotionItem>(PotionItem::class.java) {
  override fun getMeta(stack: ItemStack, item: PotionItem): MutableMap<String, *> {
    val potion = PotionUtil.getPotion(stack)

    val data = mutableMapOf<String, Any>(
      "potionType" to when (item) {
        is LingeringPotionItem -> "lingering"
        is SplashPotionItem    -> "splash"
        else                   -> "normal"
      },
      "splash" to (item is SplashPotionItem),
      "potion" to Registries.POTION.getId(potion).toString()
    )

    val effects = PotionUtil.getPotionEffects(stack)
    if (effects.isNotEmpty()) {
      data["effects"] = effects.map { effect ->
        val type = effect.effectType
        mapOf(
          "duration"  to effect.duration / 20, // ticks!
          "amplifier" to effect.amplifier,
          "name"      to type.name.string,
          "instant"   to type.isInstant,
          "color"     to type.color
        )
      }
    }

    return data
  }
}
