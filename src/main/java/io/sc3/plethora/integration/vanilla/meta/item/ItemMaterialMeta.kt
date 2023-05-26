package io.sc3.plethora.integration.vanilla.meta.item;

import net.minecraft.item.*;
import io.sc3.plethora.api.meta.BasicMetaProvider;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

import static net.minecraft.item.ToolMaterials.*;

public final class ItemMaterialMeta extends BasicMetaProvider<ItemStack> {
    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack) {
        String name = getName(stack);
        return name != null ? Collections.singletonMap("material", name) : Collections.emptyMap();
    }

    private static String getName(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ToolItem tool) {
            // TODO: this sucks
            ToolMaterial material = tool.getMaterial();
            if (WOOD.equals(material)) {
                return "wood";
            } else if (STONE.equals(material)) {
                return "stone";
            } else if (IRON.equals(material)) {
                return "iron";
            } else if (GOLD.equals(material)) {
                return "gold";
            } else if (DIAMOND.equals(material)) {
                return "diamond";
            } else if (NETHERITE.equals(material)) {
                return "netherite";
            }
            return "unknown";
        } else if (item instanceof ArmorItem armor) {
            return armor.getMaterial().getName();
        } else {
            return null;
        }
    }
}
