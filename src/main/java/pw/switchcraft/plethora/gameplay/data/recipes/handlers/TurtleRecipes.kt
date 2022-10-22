package pw.switchcraft.plethora.gameplay.data.recipes.handlers

import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.turtle.items.TurtleItemFactory
import dan200.computercraft.shared.util.ImpostorRecipe
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import pw.switchcraft.library.recipe.RecipeHandler
import pw.switchcraft.plethora.Plethora.ModId
import pw.switchcraft.plethora.Plethora.modId
import pw.switchcraft.plethora.api.PlethoraAPI
import pw.switchcraft.plethora.gameplay.data.recipes.RecipeWrapper
import pw.switchcraft.plethora.gameplay.data.recipes.inventoryChange
import java.util.function.Consumer

object TurtleRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    for (family in ComputerFamily.values()) {
      val base = TurtleItemFactory.create(-1, null, -1, family, null, null, 0, null)
      if (base.isEmpty) continue

      val nameId = family.name.lowercase()

      PlethoraAPI.instance().moduleRegistry().turtleUpgrades.forEach { upgrade ->
        val result = TurtleItemFactory.create(-1, null, -1, family, null, upgrade, -1, null)
        ShapedRecipeJsonBuilder
          .create(result.item)
          .group(String.format("%s:turtle_%s", modId, nameId))
          .pattern("#T")
          .input('T', base.item)
          .input('#', upgrade.craftingItem.item)
          .criterion("has_items", inventoryChange(base.item, upgrade.craftingItem.item))
          .offerTo(
            RecipeWrapper.wrap(ImpostorRecipe.SERIALIZER, exporter, result.nbt),
            ModId("turtle_$nameId/${upgrade.upgradeID.namespace}/${upgrade.upgradeID.path}")
          )
      }
    }
  }
}
