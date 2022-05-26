package pw.switchcraft.plethora.integration.vanilla.meta.item;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.BasicMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaProvider;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

public final class ItemMetaProviders {
    public static final IMetaProvider<ItemStack> ITEM_FOOD = new BasicMetaProvider<>(
        "Provides the hunger and saturation this foodstuff restores."
    ) {
        @Nonnull
        @Override
        public Map<String, Object> getMeta(@Nonnull ItemStack itemStack) {
            FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
            if (foodComponent == null) return Collections.emptyMap();
            return Map.of(
                "hunger", foodComponent.getHunger(),
                "saturation", foodComponent.getSaturationModifier()
            );
        }
    };
}
