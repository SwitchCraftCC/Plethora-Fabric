package pw.switchcraft.plethora.integration.computercraft.meta.item;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.items.ITurtleItem;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.ItemStackMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class TurtleItemMeta extends ItemStackMetaProvider<ITurtleItem> {
    public TurtleItemMeta() {
        super("turtle", ITurtleItem.class);
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull ITurtleItem turtle) {
        Map<String, Object> out = new HashMap<>();

        int colour = turtle.getColour(stack);
        if (colour != -1) {
            out.put("color", colour);
            out.put("colour", colour);
        }
        out.put("fuel", turtle.getFuelLevel(stack));

        out.put("left", getUpgrade(turtle.getUpgrade(stack, TurtleSide.LEFT)));
        out.put("right", getUpgrade(turtle.getUpgrade(stack, TurtleSide.RIGHT)));

        return out;
    }

    static Map<String, String> getUpgrade(ITurtleUpgrade upgrade) {
        if (upgrade == null) return null;

        Map<String, String> out = new HashMap<>(2);
        out.put("id", upgrade.getUpgradeID().toString());
        out.put("adjective", upgrade.getUnlocalisedAdjective());
        out.put("type", upgrade.getType().toString());

        return out;
    }
}
