package io.sc3.plethora.gameplay.data.recipes

import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import io.sc3.plethora.gameplay.registry.Registration.ModItems.SENSOR_MODULE

class SensorModuleUpgradeRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC
) : LevelableModuleRecipe(id, category, SENSOR_MODULE) {
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::SensorModuleUpgradeRecipe)
  }
}
