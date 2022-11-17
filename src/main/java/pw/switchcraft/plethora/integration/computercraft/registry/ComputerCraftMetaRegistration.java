package pw.switchcraft.plethora.integration.computercraft.registry;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.IMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.integration.computercraft.meta.item.*;

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
