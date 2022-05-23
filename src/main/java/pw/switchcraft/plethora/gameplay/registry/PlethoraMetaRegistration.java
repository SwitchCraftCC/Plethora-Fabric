package pw.switchcraft.plethora.gameplay.registry;

import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.IMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.gameplay.BindableModuleItemMeta;
import pw.switchcraft.plethora.integration.MetaWrapper;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

public class PlethoraMetaRegistration {
    public static void registerMetaProviders(IMetaRegistry r) {
        provider(r, "metaProvider", MetaWrapper.class, new MetaWrapper.MetaProvider());

        provider(r, "bindableModuleItem", ItemStack.class, new BindableModuleItemMeta());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(MOD_ID + ":" + name, MOD_ID, target, provider);
    }
}
