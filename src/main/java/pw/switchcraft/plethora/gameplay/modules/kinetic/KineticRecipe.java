package pw.switchcraft.plethora.gameplay.modules.kinetic;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import pw.switchcraft.library.recipe.IngredientBrew;
import pw.switchcraft.plethora.gameplay.registry.Registration;

public class KineticRecipe extends SpecialCraftingRecipe {
    private final Ingredient piston = Ingredient.ofItems(Items.PISTON);
    private final Ingredient redstone = Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS);
    private final Ingredient gold = Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS);
    private final IngredientBrew brew = new IngredientBrew(
        StatusEffects.JUMP_BOOST,
        Potions.LEAPING
    );

    private final int width = 3;
    private final int height = 3;

    private final ItemStack output;

    public KineticRecipe(Identifier id) {
        super(id);

        output = new ItemStack(Registration.ModItems.KINETIC_MODULE);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int invWidth = inv.getWidth();
        int invHeight = inv.getHeight();

        if (!fits(invWidth, invHeight)) return false;

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                ItemStack item = inv.getStack(i + j * invWidth);

                if (j == 0 || j == 2) { // Top and bottom row: redstone gold redstone
                    if (i == 1) {
                        if (!gold.test(item)) return false;
                    } else {
                        if (!redstone.test(item)) return false;
                    }
                } else { // Middle row: piston brew piston
                    if (i == 1) {
                        if (!brew.test(item)) return false;
                    } else {
                        if (!piston.test(item)) return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        return this.getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static final SpecialRecipeSerializer<KineticRecipe> SERIALIZER
        = new SpecialRecipeSerializer<>(KineticRecipe::new);
}
