package pw.switchcraft.plethora.gameplay.modules;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LevelableModuleItem extends ModuleItem {
    public LevelableModuleItem(String itemName, Settings settings) {
        super(itemName, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        int level = getLevel(stack);
        if (level < 0) return;

        int range = getEffectiveRange(stack);
        tooltip.add(new TranslatableText("item.plethora.module.level", level, range));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return super.hasGlint(stack) || getLevel(stack) > 0;
    }

    public static int getLevel(ItemStack stack) {
        if (stack == null) return 0;
        NbtCompound tag = stack.getNbt();
        return tag != null && tag.contains("level", NbtCompound.NUMBER_TYPE) ? tag.getInt("level") : 0;
    }

    public static int getEffectiveRange(int baseRange, int maxRange, int level) {
        if (maxRange <= baseRange || level <= 0) return baseRange;

        // Each level adds half of the remainder to the maximum level - so effectively the geometric sum.
        return baseRange + (int) Math.ceil((1 - Math.pow(0.5, level)) * (maxRange - baseRange));
    }

    public static int getEffectiveRange(ItemStack stack, int level) {
        if (stack == null) return 0;
        if (!(stack.getItem() instanceof LevelableModuleItem levelable)) return 0;
        return getEffectiveRange(levelable.getBaseRange(), levelable.getMaxRange(), level);
    }

    public static int getEffectiveRange(ItemStack stack) {
        return getEffectiveRange(stack, getLevel(stack));
    }

    public abstract int getBaseRange();
    public abstract int getMaxRange();
}
