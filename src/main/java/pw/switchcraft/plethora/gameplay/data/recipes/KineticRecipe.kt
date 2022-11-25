package pw.switchcraft.plethora.gameplay.data.recipes

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.GOLD_INGOTS
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.REDSTONE_DUSTS
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.PISTON
import net.minecraft.potion.Potions
import net.minecraft.recipe.Ingredient.fromTag
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import pw.switchcraft.library.recipe.BetterSpecialRecipe
import pw.switchcraft.library.recipe.IngredientBrew
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems.KINETIC_MODULE

class KineticRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC
) : BetterSpecialRecipe(id, category) {
  private val brew = IngredientBrew(StatusEffects.JUMP_BOOST, Potions.LEAPING)

  override val ingredients = listOf(
    fromTag(REDSTONE_DUSTS), fromTag(GOLD_INGOTS), fromTag(REDSTONE_DUSTS),
    ofItems(PISTON),         brew,                 ofItems(PISTON),
    fromTag(REDSTONE_DUSTS), fromTag(GOLD_INGOTS), fromTag(REDSTONE_DUSTS),
  )

  override val outputItem = ItemStack(KINETIC_MODULE)

  override fun isIgnoredInRecipeBook() = false
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::KineticRecipe)
  }
}
