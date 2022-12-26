package pw.switchcraft.plethora.gameplay.data.recipes

import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems.SENSOR_MODULE

class SensorModuleUpgradeRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC
) : LevelableModuleRecipe(id, category, SENSOR_MODULE) {
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::SensorModuleUpgradeRecipe)
  }
}
