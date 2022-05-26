package pw.switchcraft.plethora.gameplay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

public abstract class BaseItem extends Item {
    protected final String itemName;

    public BaseItem(String itemName, Settings settings) {
        super(settings);

        this.itemName = itemName;
    }

    public static NbtCompound getNbt(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        if (tag == null) stack.setNbt(tag = new NbtCompound());
        return tag;
    }

    @Override
    public String getTranslationKey() {
        return "item." + MOD_ID + "." + itemName;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable(getTranslationKey(stack) + ".desc")
            .formatted(Formatting.GRAY));
    }
}
