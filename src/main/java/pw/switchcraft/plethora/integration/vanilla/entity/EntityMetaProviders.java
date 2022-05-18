package pw.switchcraft.plethora.integration.vanilla.entity;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import pw.switchcraft.plethora.api.meta.BaseMetaProvider;
import pw.switchcraft.plethora.api.meta.BasicMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaProvider;
import pw.switchcraft.plethora.api.method.IPartialContext;

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
            return Collections.singletonMap("item", context.makePartialChild(context.getTarget().getStack()).getMeta());
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
