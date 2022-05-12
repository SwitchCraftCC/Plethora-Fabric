package pw.switchcraft.plethora.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeMatcher;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public class IngredientBrew extends Ingredient {
    private final StatusEffect effect;

    private final ItemStack[] basicStacks;
    private IntList packed;

    public IngredientBrew(StatusEffect effect, Potion potion) {
        super(Stream.empty());

        this.effect = effect;

        this.basicStacks = new ItemStack[] {
            PotionUtil.setPotion(new ItemStack(Items.POTION), potion),
            PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion),
            PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), potion)
        };
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        return basicStacks;
    }

    @Override
    @Nonnull
    public IntList getMatchingItemIds() {
        if (packed != null) return packed;

        packed = new IntArrayList();
        for (ItemStack stack : basicStacks) packed.add(RecipeMatcher.getItemId(stack));
        packed.sort(IntComparators.NATURAL_COMPARATOR);

        return packed;
    }

    @Override
    public boolean test(@Nullable ItemStack target) {
        if (target == null || target.isEmpty()) return false;

        for (StatusEffectInstance effect : PotionUtil.getPotionEffects(target)) {
            if (effect.getEffectType() == this.effect) return true;
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
