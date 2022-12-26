package io.sc3.plethora.integration.computercraft.meta.item;

import dan200.computercraft.shared.media.items.PrintoutItem;
import net.minecraft.item.ItemStack;
import io.sc3.plethora.api.meta.ItemStackMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class PrintoutItemMeta extends ItemStackMetaProvider<PrintoutItem> {
    public PrintoutItemMeta() {
        super("printout", PrintoutItem.class);
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull PrintoutItem printout) {
        Map<String, Object> out = new HashMap<>(4);
        out.put("type", printout.getType().toString());
        out.put("title", PrintoutItem.getTitle(stack));
        out.put("pages", PrintoutItem.getPageCount(stack));

        Map<Integer, String> lines = new HashMap<>();
        String[] lineArray = PrintoutItem.getText(stack);
        for (int i = 0; i < lineArray.length; i++) {
            lines.put(i + 1, lineArray[i]);
        }
        out.put("lines", lines);

        return out;
    }
}
