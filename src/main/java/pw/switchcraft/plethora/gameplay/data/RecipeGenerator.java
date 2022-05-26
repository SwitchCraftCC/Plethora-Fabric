package pw.switchcraft.plethora.gameplay.data;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.pocket.items.PocketComputerItemFactory;
import dan200.computercraft.shared.turtle.items.TurtleItemFactory;
import dan200.computercraft.shared.util.ImpostorRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.PlethoraAPI;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Consumer;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

public class RecipeGenerator extends FabricRecipeProvider {
    public RecipeGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        RecipeRegistry.addDynamicRecipes(exporter);

        addTurtleUpgrades(exporter);
        addPocketUpgrades(exporter);
    }

    /**
     * @see "dan200.computercraft.data.RecipeGenerator#turtleUpgrades"
     */
    private void addTurtleUpgrades(@Nonnull Consumer<RecipeJsonProvider> exporter) {
        for (ComputerFamily family : ComputerFamily.values()) {
            ItemStack base = TurtleItemFactory.create(-1, null, -1, family, null, null, 0, null);
            if (base.isEmpty()) continue;

            String nameId = family.name().toLowerCase(Locale.ROOT);

            PlethoraAPI.instance().moduleRegistry().getTurtleUpgrades().forEach(upgrade -> {
                ItemStack result = TurtleItemFactory.create(-1, null, -1, family, null, upgrade, -1, null);
                ShapedRecipeJsonBuilder
                    .create(result.getItem())
                    .group(String.format("%s:turtle_%s", MOD_ID, nameId))
                    .pattern("#T")
                    .input('T', base.getItem())
                    .input('#', upgrade.getCraftingItem().getItem())
                    .criterion("has_items",
                        inventoryChange(base.getItem(), upgrade.getCraftingItem().getItem()))
                    .offerTo(
                        RecipeWrapper.wrap(ImpostorRecipe.SERIALIZER, exporter, result.getNbt()),
                        new Identifier(MOD_ID, String.format("turtle_%s/%s/%s",
                            nameId, upgrade.getUpgradeID().getNamespace(), upgrade.getUpgradeID().getPath()
                        ))
                    );
            });
        }
    }

    /**
     * @see "dan200.computercraft.data.RecipeGenerator#pocketUpgrades"
     */
    private void addPocketUpgrades(@Nonnull Consumer<RecipeJsonProvider> exporter) {
        for (ComputerFamily family : ComputerFamily.values()) {
            ItemStack base = PocketComputerItemFactory.create(-1, null, -1, family, null);
            if (base.isEmpty()) continue;

            String nameId = family.name().toLowerCase(Locale.ROOT);

            PlethoraAPI.instance().moduleRegistry().getPocketUpgrades().forEach(upgrade -> {
                ItemStack result = PocketComputerItemFactory.create(-1, null, -1, family, upgrade);
                ShapedRecipeJsonBuilder
                    .create(result.getItem())
                    .group(String.format("%s:pocket_%s", MOD_ID, nameId))
                    .pattern("#")
                    .pattern("P")
                    .input('P', base.getItem())
                    .input('#', upgrade.getCraftingItem().getItem())
                    .criterion("has_items",
                        inventoryChange(base.getItem(), upgrade.getCraftingItem().getItem()))
                    .offerTo(
                        RecipeWrapper.wrap(ImpostorRecipe.SERIALIZER, exporter, result.getNbt()),
                        new Identifier(MOD_ID, String.format("pocket_%s/%s/%s",
                            nameId, upgrade.getUpgradeID().getNamespace(), upgrade.getUpgradeID().getPath()
                        ))
                    );
            });
        }
    }

    private static InventoryChangedCriterion.Conditions inventoryChange(TagKey<Item> tag) {
        return InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(tag).build());
    }

    private static InventoryChangedCriterion.Conditions inventoryChange(ItemConvertible... items) {
        return InventoryChangedCriterion.Conditions.items(items);
    }
}
