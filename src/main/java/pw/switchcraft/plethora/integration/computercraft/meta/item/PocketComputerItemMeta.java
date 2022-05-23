package pw.switchcraft.plethora.integration.computercraft.meta.item;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.ItemStackMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class PocketComputerItemMeta extends ItemStackMetaProvider<ItemPocketComputer> {
    public PocketComputerItemMeta() {
        super("pocket", ItemPocketComputer.class);
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull ItemPocketComputer pocket) {
        Map<String, Object> out = new HashMap<>(2);

        int colour = pocket.getColour(stack);
        if (colour != -1) {
            out.put("color", colour);
            out.put("colour", colour);
        }

        out.put("back", getUpgrade(ItemPocketComputer.getUpgrade(stack)));

        return out;
    }

    private static Map<String, String> getUpgrade(IPocketUpgrade upgrade) {
        if (upgrade == null) return null;

        Map<String, String> out = new HashMap<>(2);
        out.put("id", upgrade.getUpgradeID().toString());
        out.put("adjective", upgrade.getUnlocalisedAdjective());

        return out;
    }
}
