package io.sc3.plethora.gameplay.data.recipes.handlers

import io.sc3.library.recipe.RecipeHandler

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
