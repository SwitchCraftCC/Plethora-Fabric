package pw.switchcraft.plethora.gameplay.modules.laser;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.registry.Registration;
import pw.switchcraft.plethora.util.IngredientEnchanted;

import java.util.Map;

public class LaserRecipe extends SpecialCraftingRecipe {
    private final Ingredient iron = Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS);
    private final Ingredient diamond = Ingredient.fromTag(ConventionalItemTags.DIAMONDS);
    private final Ingredient glass = Ingredient.fromTag(ConventionalItemTags.GLASS_BLOCKS);
    private final IngredientEnchanted enchanted = new IngredientEnchanted(Map.of(
        Enchantments.FLAME, 1,
        Enchantments.FIRE_ASPECT, 1
    ));

    private final int width = 3;
    private final int height = 3;

    private final ItemStack output;

    public LaserRecipe(Identifier id) {
        super(id);

        output = new ItemStack(Registration.ModItems.LASER_MODULE);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int invWidth = inv.getWidth();
        int invHeight = inv.getHeight();

        if (!fits(invWidth, invHeight)) return false;

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                ItemStack item = inv.getStack(i + j * invWidth);

                if (j == 0 || (i == 2 && j == 2)) { // Top row and bottom right slot: iron ingots
                    if (!iron.test(item)) return false;
                } else if (i == 0 && j == 1) { // Middle left slot: glass blocks
                    if (!glass.test(item)) return false;
                } else if (i == 1 && j == 1) { // Middle slot: diamonds
                    if (!diamond.test(item)) return false;
                } else if (i == 2 && j == 1) { // Middle right slot: enchanted items
                    if (!enchanted.test(item)) return false;
                } else if (!item.isEmpty()) {
                    return false;
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

    public static final SpecialRecipeSerializer<LaserRecipe> SERIALIZER
        = new SpecialRecipeSerializer<>(LaserRecipe::new);
}
