package pw.switchcraft.plethora.gameplay.data.recipes.handlers

import dan200.computercraft.shared.Registry
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry.RECIPE_SERIALIZER
import net.minecraft.util.registry.Registry.register
import pw.switchcraft.library.recipe.BetterComplexRecipeJsonBuilder
import pw.switchcraft.library.recipe.RecipeHandler
import pw.switchcraft.plethora.PlethoraKt.ModId
import pw.switchcraft.plethora.gameplay.data.recipes.NeuralInterfaceRecipe
import pw.switchcraft.plethora.gameplay.data.recipes.inventoryChange
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems
import java.util.function.Consumer

object MiscRecipes : RecipeHandler {
  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("neural_interface"), NeuralInterfaceRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Manipulator Mark I
    ShapedRecipeJsonBuilder
      .create(ModItems.MANIPULATOR_MARK_1)
      .pattern("GCG")
      .pattern("RMR")
      .pattern("III")
      .input('C', ConventionalItemTags.GOLD_INGOTS)
      .input('G', ConventionalItemTags.GLASS_BLOCKS)
      .input('I', ConventionalItemTags.IRON_INGOTS)
      .input('M', Registry.ModItems.CABLE)
      .input('R', ConventionalItemTags.REDSTONE_DUSTS)
      .hasComputer()
      .offerTo(exporter)

    // Manipulator Mark II
    ShapedRecipeJsonBuilder
      .create(ModItems.MANIPULATOR_MARK_2)
      .pattern("CCC")
      .pattern("RMR")
      .pattern("III")
      .input('C', ConventionalItemTags.GOLD_INGOTS)
      .input('I', ConventionalItemTags.IRON_INGOTS)
      .input('M', ModItems.MANIPULATOR_MARK_1)
      .input('R', ConventionalItemTags.REDSTONE_DUSTS)
      .hasComputer()
      .offerTo(exporter)

    // Neural Connector
    ShapedRecipeJsonBuilder
      .create(ModItems.NEURAL_CONNECTOR)
      .pattern("  R")
      .pattern("IIR")
      .pattern("IEI")
      .input('E', Items.ENDER_PEARL)
      .input('I', ConventionalItemTags.IRON_INGOTS)
      .input('R', ConventionalItemTags.REDSTONE_DUSTS)
      .hasComputer()
      .offerTo(exporter)

    // Neural Interface
    BetterComplexRecipeJsonBuilder(ModItems.NEURAL_INTERFACE, NeuralInterfaceRecipe.recipeSerializer)
      .hasComputer()
      .offerTo(exporter)

    // Redstone Integrator
    ShapedRecipeJsonBuilder
      .create(ModItems.REDSTONE_INTEGRATOR)
      .pattern("SRS")
      .pattern("RCR")
      .pattern("SRS")
      .input('S', Items.STONE)
      .input('C', Registry.ModItems.CABLE)
      .input('R', ConventionalItemTags.REDSTONE_DUSTS)
      .hasComputer()
      .offerTo(exporter)
  }

  private val computerCriteria = mapOf(
    "has_computer_normal"   to inventoryChange(Registry.ModItems.COMPUTER_NORMAL),
    "has_computer_advanced" to inventoryChange(Registry.ModItems.COMPUTER_ADVANCED),
    "has_turtle_normal"     to inventoryChange(Registry.ModItems.TURTLE_NORMAL),
    "has_turtle_advanced"   to inventoryChange(Registry.ModItems.TURTLE_ADVANCED),
    "has_pocket_normal"     to inventoryChange(Registry.ModItems.POCKET_COMPUTER_NORMAL),
    "has_pocket_advanced"   to inventoryChange(Registry.ModItems.POCKET_COMPUTER_ADVANCED),
  )

  private fun CraftingRecipeJsonBuilder.hasComputer() = apply {
    computerCriteria.forEach { criterion(it.key, it.value) }
  }

  private fun BetterComplexRecipeJsonBuilder<*, *>.hasComputer() = apply {
    computerCriteria.forEach { criterion(it.key, it.value) }
  }
}
