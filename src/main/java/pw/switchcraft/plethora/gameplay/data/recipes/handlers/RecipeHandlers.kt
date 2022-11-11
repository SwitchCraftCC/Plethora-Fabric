package pw.switchcraft.plethora.gameplay.data.recipes.handlers

import pw.switchcraft.library.recipe.RecipeHandler

object RecipeHandlers {
  val RECIPE_HANDLERS by lazy { listOf(
    ModuleRecipes,
    MiscRecipes,
  )}

  @JvmStatic
  fun registerSerializers() {
    RECIPE_HANDLERS.forEach(RecipeHandler::registerSerializers)
  }
}
