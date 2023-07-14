package io.sc3.plethora.gameplay.data.recipes.handlers

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import io.sc3.library.recipe.RecipeHandler
import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.Plethora.modId
import io.sc3.plethora.gameplay.data.recipes.RecipeWrapper
import io.sc3.plethora.gameplay.data.recipes.inventoryChange
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.recipe.book.RecipeCategory
import java.util.function.Consumer

class PocketRecipes(private val upgrades: PocketUpgradeDataProvider) : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    for (family in ComputerFamily.values()) {
      val base = PocketComputerItem.create(-1, null, -1, family, null);
      if (base.isEmpty) continue

      val nameId = family.name.lowercase()

      upgrades.generatedUpgrades.forEach { upgrade ->
        val result = PocketComputerItem.create(-1, null, -1, family, UpgradeData.ofDefault(upgrade));
        ShapedRecipeJsonBuilder
          .create(RecipeCategory.MISC, result.item)
          .group(String.format("%s:pocket_%s", modId, nameId))
          .pattern("#P")
          .input('P', base.item)
          .input('#', upgrade.craftingItem.item)
          .criterion("has_items", inventoryChange(base.item, upgrade.craftingItem.item))
          .offerTo(
            RecipeWrapper.wrap(ModRegistry.RecipeSerializers.IMPOSTOR_SHAPED.get(), exporter, result.nbt),
            ModId("pocket_$nameId/${upgrade.upgradeID.namespace}/${upgrade.upgradeID.path}")
          )
      }
    }
  }
}
