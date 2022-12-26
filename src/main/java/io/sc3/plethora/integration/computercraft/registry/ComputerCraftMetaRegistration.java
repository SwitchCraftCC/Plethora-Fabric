package io.sc3.plethora.integration.computercraft.registry;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.item.ItemStack;
import io.sc3.plethora.api.meta.IMetaProvider;
import io.sc3.plethora.api.meta.IMetaRegistry;
import io.sc3.plethora.integration.computercraft.meta.item.*;

public class ComputerCraftMetaRegistration {
    public static void registerMetaProviders(IMetaRegistry r) {
        provider(r, "computer", ItemStack.class, new ComputerItemMeta());
        provider(r, "media", ItemStack.class, new MediaItemMeta());
        provider(r, "pocket", ItemStack.class, new PocketComputerItemMeta());
        provider(r, "printout", ItemStack.class, new PrintoutItemMeta());
        provider(r, "turtle", ItemStack.class, new TurtleItemMeta());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(ComputerCraftAPI.MOD_ID + ":" + name, ComputerCraftAPI.MOD_ID, target, provider);
    }
}
