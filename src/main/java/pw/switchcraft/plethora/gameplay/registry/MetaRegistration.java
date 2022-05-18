package pw.switchcraft.plethora.gameplay.registry;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.IMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.integration.vanilla.entity.EntityMetaProviders;
import pw.switchcraft.plethora.integration.vanilla.item.BasicItemMeta;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

class MetaRegistration {
    static void registerPlethoraMetaProviders(IMetaRegistry r) {
        // TODO: Move vanilla registration to ../../integration/vanilla
        provider(r, "itemEntity", ItemEntity.class, EntityMetaProviders.ITEM_ENTITY);
        provider(r, "sheepEntity", SheepEntity.class, EntityMetaProviders.SHEEP_ENTITY);
        provider(r, "basicItem", ItemStack.class, new BasicItemMeta());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(name, MOD_ID, target, provider);
    }
}
