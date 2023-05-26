package io.sc3.plethora.gameplay.registry;

import net.minecraft.item.ItemStack;
import io.sc3.plethora.Plethora;
import io.sc3.plethora.api.meta.IMetaProvider;
import io.sc3.plethora.api.meta.IMetaRegistry;
import io.sc3.plethora.gameplay.BindableModuleItemMeta;
import io.sc3.plethora.integration.MetaWrapper;

public class PlethoraMetaRegistration {
    public static void registerMetaProviders(IMetaRegistry r) {
        provider(r, "metaProvider", MetaWrapper.class, new MetaWrapper.MetaProvider());

        provider(r, "bindableModuleItem", ItemStack.class, new BindableModuleItemMeta());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(Plethora.modId + ":" + name, Plethora.modId, target, provider);
    }
}
