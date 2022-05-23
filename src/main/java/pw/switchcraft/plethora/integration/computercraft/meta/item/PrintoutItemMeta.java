package pw.switchcraft.plethora.integration.computercraft.meta.item;

import dan200.computercraft.shared.media.items.ItemPrintout;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.ItemStackMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class PrintoutItemMeta extends ItemStackMetaProvider<ItemPrintout> {
    public PrintoutItemMeta() {
        super("printout", ItemPrintout.class);
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull ItemPrintout printout) {
        Map<String, Object> out = new HashMap<>(4);
        out.put("type", printout.getType().toString());
        out.put("title", ItemPrintout.getTitle(stack));
        out.put("pages", ItemPrintout.getPageCount(stack));

        Map<Integer, String> lines = new HashMap<>();
        String[] lineArray = ItemPrintout.getText(stack);
        for (int i = 0; i < lineArray.length; i++) {
            lines.put(i + 1, lineArray[i]);
        }
        out.put("lines", lines);

        return out;
    }
}
