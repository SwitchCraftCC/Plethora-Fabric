package pw.switchcraft.plethora.gameplay.data.recipes

import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.Identifier
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems.SCANNER_MODULE

class ScannerModuleUpgradeRecipe(id: Identifier) : LevelableModuleRecipe(id, SCANNER_MODULE) {
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer { ScannerModuleUpgradeRecipe(it) }
  }
}
