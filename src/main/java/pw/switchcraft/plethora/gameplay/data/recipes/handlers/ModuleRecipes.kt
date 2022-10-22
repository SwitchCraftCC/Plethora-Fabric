package pw.switchcraft.plethora.gameplay.data.recipes.handlers

import dan200.computercraft.shared.Registry
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry.RECIPE_SERIALIZER
import net.minecraft.util.registry.Registry.register
import pw.switchcraft.library.recipe.BetterComplexRecipeJsonBuilder
import pw.switchcraft.library.recipe.RecipeHandler
import pw.switchcraft.plethora.Plethora.ModId
import pw.switchcraft.plethora.gameplay.data.recipes.*
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems
import java.util.function.Consumer

object ModuleRecipes : RecipeHandler {
  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("kinetic_module"), KineticRecipe.recipeSerializer)
    register(RECIPE_SERIALIZER, ModId("laser_module"), LaserRecipe.recipeSerializer)
    register(RECIPE_SERIALIZER, ModId("scanner_module_upgrade"), ScannerModuleUpgradeRecipe.recipeSerializer)
    register(RECIPE_SERIALIZER, ModId("sensor_module_upgrade"), SensorModuleUpgradeRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Overlay Glasses
    ShapedRecipeJsonBuilder
      .create(ModItems.GLASSES_MODULE)
      .pattern("MIM")
      .pattern("GGG")
      .pattern("IAI")
      .input('A', Items.IRON_HELMET)
      .input('G', Registry.ModItems.MONITOR_ADVANCED)
      .input('I', ConventionalItemTags.IRON_INGOTS)
      .input('M', Registry.ModItems.WIRELESS_MODEM_NORMAL)
      .hasModuleHandler()
      .offerTo(exporter)

    // Introspection Module
    ShapedRecipeJsonBuilder
      .create(ModItems.INTROSPECTION_MODULE)
      .pattern("GCG")
      .pattern("CHC")
      .pattern("GCG")
      .input('C', Items.ENDER_CHEST)
      .input('G', ConventionalItemTags.GOLD_INGOTS)
      .input('H', Items.DIAMOND_HELMET)
      .hasModuleHandler()
      .offerTo(exporter)

    // Keyboard
    ShapedRecipeJsonBuilder
      .create(ModItems.KEYBOARD_MODULE)
      .pattern("  C")
      .pattern("SSI")
      .pattern("SSS")
      .input('C', Registry.ModItems.CABLE)
      .input('I', ConventionalItemTags.IRON_INGOTS)
      .input('S', Items.STONE)
      .hasModuleHandler()
      .offerTo(exporter)

    // Block Scanner
    ShapedRecipeJsonBuilder
      .create(ModItems.SCANNER_MODULE)
      .pattern("EDE")
      .pattern("IOI")
      .pattern("III")
      .input('D', Items.DIRT)
      .input('E', Items.ENDER_EYE)
      .input('I', ConventionalItemTags.IRON_INGOTS)
      .input('O', Items.OBSERVER)
      .hasModuleHandler()
      .offerTo(exporter)

    // Entity Sensor
    ShapedRecipeJsonBuilder
      .create(ModItems.SENSOR_MODULE)
      .pattern("ERE")
      .pattern("IOI")
      .pattern("III")
      .input('E', Items.ENDER_EYE)
      .input('I', ConventionalItemTags.IRON_INGOTS)
      .input('O', Items.OBSERVER)
      .input('R', Items.ROTTEN_FLESH)
      .hasModuleHandler()
      .offerTo(exporter)

    // Kinetic Augment
    BetterComplexRecipeJsonBuilder(ModItems.KINETIC_MODULE, KineticRecipe.recipeSerializer)
      .hasModuleHandler()
      .offerTo(exporter)

    // Frickin' Laser Beam
    BetterComplexRecipeJsonBuilder(ModItems.LASER_MODULE, LaserRecipe.recipeSerializer)
      .hasModuleHandler()
      .offerTo(exporter)

    // Module Upgrades
    BetterComplexRecipeJsonBuilder(ModItems.SCANNER_MODULE, ScannerModuleUpgradeRecipe.recipeSerializer)
      .criterion("has_scanner", RecipeProvider.conditionsFromItem(ModItems.SCANNER_MODULE))
      .offerTo(exporter, ModId("scanner_module_upgrade"))

    BetterComplexRecipeJsonBuilder(ModItems.SENSOR_MODULE, SensorModuleUpgradeRecipe.recipeSerializer)
      .criterion("has_sensor", RecipeProvider.conditionsFromItem(ModItems.SENSOR_MODULE))
      .offerTo(exporter, ModId("sensor_module_upgrade"))
  }

  private val moduleHandlerCriteria = mapOf(
    "has_manipulator_mark_1" to inventoryChange(ModItems.MANIPULATOR_MARK_1),
    "has_manipulator_mark_2" to inventoryChange(ModItems.MANIPULATOR_MARK_2),
    "has_neural_interface"   to inventoryChange(ModItems.NEURAL_INTERFACE)
  )

  private fun CraftingRecipeJsonBuilder.hasModuleHandler() = apply {
    moduleHandlerCriteria.forEach { criterion(it.key, it.value) }
  }

  private fun BetterComplexRecipeJsonBuilder<*, *>.hasModuleHandler() = apply {
    moduleHandlerCriteria.forEach { criterion(it.key, it.value) }
  }
}
