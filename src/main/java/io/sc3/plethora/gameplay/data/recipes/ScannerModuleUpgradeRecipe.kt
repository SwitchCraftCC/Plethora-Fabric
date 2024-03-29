package io.sc3.plethora.gameplay.data.recipes

import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import io.sc3.plethora.gameplay.registry.Registration.ModItems.SCANNER_MODULE

class ScannerModuleUpgradeRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC
) : LevelableModuleRecipe(id, category, SCANNER_MODULE) {
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::ScannerModuleUpgradeRecipe)
  }
}
