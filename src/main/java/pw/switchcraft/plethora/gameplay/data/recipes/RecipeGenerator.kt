package pw.switchcraft.plethora.gameplay.data.recipes

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.ItemConvertible
import pw.switchcraft.plethora.gameplay.data.recipes.handlers.RecipeHandlers.RECIPE_HANDLERS
import java.util.function.Consumer

class RecipeGenerator(generator: FabricDataGenerator) : FabricRecipeProvider(generator) {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    RECIPE_HANDLERS.forEach { it.generateRecipes(exporter) }
  }
}

fun inventoryChange(vararg items: ItemConvertible): InventoryChangedCriterion.Conditions =
  InventoryChangedCriterion.Conditions.items(*items)
