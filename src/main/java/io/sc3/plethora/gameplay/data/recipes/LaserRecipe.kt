package io.sc3.plethora.gameplay.data.recipes

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient.EMPTY
import net.minecraft.recipe.Ingredient.fromTag
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import io.sc3.library.recipe.BetterSpecialRecipe
import io.sc3.library.recipe.IngredientEnchanted
import io.sc3.plethora.gameplay.registry.Registration.ModItems.LASER_MODULE

class LaserRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC
) : BetterSpecialRecipe(id, category) {
  private val enchanted = IngredientEnchanted(mapOf(
    Enchantments.FLAME to 1,
    Enchantments.FIRE_ASPECT to 1
  ))

  override val ingredients = listOf(
    fromTag(IRON_INGOTS),  fromTag(IRON_INGOTS), fromTag(IRON_INGOTS),
    fromTag(GLASS_BLOCKS), fromTag(DIAMONDS),    enchanted,
    EMPTY,                 EMPTY,                fromTag(IRON_INGOTS),
  )

  override val outputItem = ItemStack(LASER_MODULE)

  override fun isIgnoredInRecipeBook() = false
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::LaserRecipe)
  }
}
