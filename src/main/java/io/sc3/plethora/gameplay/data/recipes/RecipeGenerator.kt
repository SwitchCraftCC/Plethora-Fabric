package io.sc3.plethora.gameplay.data.recipes

import io.sc3.plethora.gameplay.data.recipes.handlers.RecipeHandlers.RECIPE_HANDLERS
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.ItemConvertible
import java.util.function.Consumer

class RecipeGenerator(out: FabricDataOutput) : FabricRecipeProvider(out) {
  override fun generate(exporter: Consumer<RecipeJsonProvider>) {
    RECIPE_HANDLERS.forEach { it.generateRecipes(exporter) }
  }
}

fun inventoryChange(vararg items: ItemConvertible): InventoryChangedCriterion.Conditions =
  InventoryChangedCriterion.Conditions.items(*items)
