package pw.switchcraft.plethora.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class IngredientEnchanted extends Ingredient {
    /** enchantment, min level **/
    private final Map<Enchantment, Integer> enchantments;

    private ItemStack[] stacks;
    private IntList packed;

    public IngredientEnchanted(Map<Enchantment, Integer> enchantments) {
        super(Stream.empty());

        this.enchantments = enchantments;
    }

    public static ItemStack[] getItemsEnchantedWith(Map<Enchantment, Integer> enchantments) {
        if (enchantments.isEmpty()) return new ItemStack[0];

        // Find any item which matches this predicate
        ArrayList<ItemStack> stacks = new ArrayList<>();
        enchantments.forEach((enchantment, minLevel) -> {
            for (Item item : Registry.ITEM) {
                if (enchantment.type != null && enchantment.type.isAcceptableItem(item)) {
                    for (int level = minLevel; level <= enchantment.getMaxLevel(); level++) {
                        ItemStack stack = new ItemStack(item);
                        EnchantmentHelper.set(Collections.singletonMap(enchantment, level), stack);
                        stacks.add(stack);
                    }
                }
            }
        });

        return stacks.toArray(new ItemStack[0]);
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        if (stacks != null) return stacks;
        return this.stacks = getItemsEnchantedWith(this.enchantments);
    }

    @Override
    @Nonnull
    public IntList getMatchingItemIds() {
        if (packed != null) return packed;

        packed = new IntArrayList();
        for (ItemStack stack : getMatchingStacks()) packed.add(RecipeMatcher.getItemId(stack));
        packed.sort(IntComparators.NATURAL_COMPARATOR);

        return packed;
    }

    @Override
    public boolean test(@Nullable ItemStack target) {
        if (target == null || target.isEmpty()) return false;

        NbtList nbtEnchantments = target.getItem() == Items.ENCHANTED_BOOK
            ? EnchantedBookItem.getEnchantmentNbt(target)
            : target.getEnchantments();

        for (int i = 0; i < nbtEnchantments.size(); i++) {
            NbtCompound tag = nbtEnchantments.getCompound(i);
            Enchantment itemEnchant = Registry.ENCHANTMENT.get(new Identifier(tag.getString("id")));
            if (enchantments.containsKey(itemEnchant)) {
                return (int) tag.getShort("lvl") >= enchantments.get(itemEnchant);
            }
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
