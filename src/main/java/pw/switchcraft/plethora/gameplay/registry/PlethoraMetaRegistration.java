package pw.switchcraft.plethora.gameplay.registry;

import pw.switchcraft.plethora.api.meta.IMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.integration.MetaWrapper;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

public class PlethoraMetaRegistration {
    public static void registerMetaProviders(IMetaRegistry r) {
        provider(r, "metaProvider", MetaWrapper.class, new MetaWrapper.MetaProvider());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(name, MOD_ID, target, provider);
    }
}
