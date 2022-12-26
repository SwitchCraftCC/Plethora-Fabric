package io.sc3.plethora.integration.computercraft.meta.item;

import com.google.common.base.Strings;
import dan200.computercraft.shared.computer.items.IComputerItem;
import net.minecraft.item.ItemStack;
import io.sc3.plethora.api.meta.ItemStackMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class ComputerItemMeta extends ItemStackMetaProvider<IComputerItem> {
    public ComputerItemMeta() {
        super("computer", IComputerItem.class);
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull IComputerItem item) {
        Map<String, Object> data = new HashMap<>(3);

        int id = item.getComputerID(stack);
        if (id >= 0) data.put("id", id);


        String label = item.getLabel(stack);
        if (!Strings.isNullOrEmpty(label)) data.put("label", label);
        data.put("family", item.getFamily().toString());

        return data;
    }
}
