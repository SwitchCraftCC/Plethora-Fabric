package io.sc3.plethora.integration.vanilla.meta.entity;

import dan200.computercraft.api.detail.VanillaDetailRegistries;
import io.sc3.plethora.api.meta.BaseMetaProvider;
import io.sc3.plethora.api.meta.BasicMetaProvider;
import io.sc3.plethora.api.meta.IMetaProvider;
import io.sc3.plethora.api.method.IPartialContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class EntityMetaProviders {
    public static final IMetaProvider<ItemEntity> ITEM_ENTITY = new BaseMetaProvider<ItemEntity>(
        "Provides the stack of a dropped item"
    ) {
        @Nonnull
        @Override
        public Map<String, ?> getMeta(@Nonnull IPartialContext<ItemEntity> context) {
            var stack = context.getTarget().getStack();
            var data = VanillaDetailRegistries.ITEM_STACK.getDetails(stack);
            return Collections.singletonMap("item", data);
        }
    };

    public static final IMetaProvider<SheepEntity> SHEEP_ENTITY = new BasicMetaProvider<SheepEntity>(
        "Provides the wool colour of the sheep."
    ) {
        @Nonnull
        @Override
        public Map<String, ?> getMeta(@Nonnull SheepEntity sheep) {
            Map<String, Object> meta = new HashMap<>(2);
            String color = sheep.getColor().getName();
            meta.put("woolColour", color);
            meta.put("woolColor", color);
            return meta;
        }
    };
}
