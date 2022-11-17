package pw.switchcraft.plethora.gameplay.data.recipes

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider
import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.ItemConvertible
import pw.switchcraft.plethora.gameplay.data.recipes.handlers.PocketRecipes
import pw.switchcraft.plethora.gameplay.data.recipes.handlers.RecipeHandlers.RECIPE_HANDLERS
import pw.switchcraft.plethora.gameplay.data.recipes.handlers.TurtleRecipes
import java.util.function.Consumer

class RecipeGenerator(
  generator: FabricDataGenerator,
  private val turtleUpgrades: TurtleUpgradeDataProvider,
  private val pocketUpgrades: PocketUpgradeDataProvider,
) : FabricRecipeProvider(generator) {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    RECIPE_HANDLERS.forEach { it.generateRecipes(exporter) }
    TurtleRecipes(turtleUpgrades).generateRecipes(exporter)
    PocketRecipes(pocketUpgrades).generateRecipes(exporter)
  }
}

fun inventoryChange(vararg items: ItemConvertible): InventoryChangedCriterion.Conditions =
  InventoryChangedCriterion.Conditions.items(*items)
