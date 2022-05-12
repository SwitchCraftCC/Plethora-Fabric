package pw.switchcraft.plethora.gameplay.modules;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class LevelableModuleRecipe extends ShapelessRecipe {
    public LevelableModuleRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
        super(id, group, output, input);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        // `isDynamic` in 1.12
        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack output = getOutput();
        for (int i = 0; i < craftingInventory.size(); i++) {
            ItemStack stack = craftingInventory.getStack(i);

            // Look for a levelable module equivalent to our defined output item
            if (!(stack.getItem() instanceof LevelableModuleItem)
                || stack.getItem() != output.getItem()) {
                continue;
            }

            ItemStack result = stack.copy();
            result.setCount(1);

            // Only increment the level if the module is not already at the max - i.e. only if the effective radius is
            // different to before
            int oldLevel = LevelableModuleItem.getLevel(stack);
            int oldRange = LevelableModuleItem.getEffectiveRange(stack);
            int newRange = LevelableModuleItem.getEffectiveRange(stack, oldLevel + 1);

            if (oldRange == newRange) return ItemStack.EMPTY;

            // Increment the level by updating the NBT of the result item
            NbtCompound tag = result.getNbt();
            if (tag == null) result.setNbt(tag = new NbtCompound());
            tag.putInt("level", oldLevel + 1);

            return result;
        }

        return output.copy();
    }
}
