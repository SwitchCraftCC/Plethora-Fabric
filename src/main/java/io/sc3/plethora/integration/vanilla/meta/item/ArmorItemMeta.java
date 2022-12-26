package io.sc3.plethora.integration.vanilla.meta.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import io.sc3.plethora.api.meta.ItemStackMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Meta provider for amour properties. Material is handled in {@link ItemMaterialMeta}.
 */
public final class ArmorItemMeta extends ItemStackMetaProvider<ArmorItem> {
    public ArmorItemMeta() {
        super(ArmorItem.class, "Provides type and colour of amour.");
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull ArmorItem armor) {
        HashMap<String, Object> data = new HashMap<>(3);
        data.put("armorType", armor.getSlotType().getName());

        if (armor instanceof DyeableArmorItem dyeable) {
            int color = dyeable.getColor(stack);
            if (color >= 0) {
                data.put("color", color);
                data.put("colour", color);
            }
        }

        return data;
    }
}
