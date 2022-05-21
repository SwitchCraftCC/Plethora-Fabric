package pw.switchcraft.plethora.integration.vanilla.meta.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.api.meta.BasicMetaProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static dan200.computercraft.shared.util.NBTUtil.getNBTHash;

public final class BasicItemMeta extends BasicMetaProvider<ItemStack> {
    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) return Collections.emptyMap();

        HashMap<String, Object> data = new HashMap<>();
        fillBasicMeta(data, stack);

        Text displayText = stack.getName();
        String display = displayText != null ? displayText.getString() : null;
        data.put("displayName", display == null || display.isEmpty() ? stack.getTranslationKey() : display);
        data.put("rawName", stack.getTranslationKey());

        data.put("maxCount", stack.getMaxCount());
        data.put("maxDamage", stack.getMaxDamage());

        if (stack.getItem().isItemBarVisible(stack)) {
            data.put("durability", stack.getItem().getItemBarStep(stack));
        }

        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("display", NbtElement.COMPOUND_TYPE)) {
            NbtCompound displayNbt = nbt.getCompound("display");
            if (displayNbt.contains("Lore", NbtElement.LIST_TYPE)) {
                NbtList loreNbt = displayNbt.getList("Lore", NbtElement.STRING_TYPE);
                data.put("lore", loreNbt.stream().map(NbtElement::asString).collect(Collectors.toList()));
            }
        }

        return data;
    }

    @Nonnull
    public static HashMap<String, Object> getBasicMeta(@Nonnull ItemStack stack) {
        HashMap<String, Object> data = new HashMap<>();
        fillBasicMeta(data, stack);
        return data;
    }

    public static void fillBasicMeta(HashMap<String, Object> data, ItemStack stack) {
        data.put("name", Registry.ITEM.getId(stack.getItem()).toString());
        data.put("damage", stack.getDamage());
        data.put("count", stack.getCount());
        data.put("nbtHash", getNbtHash(stack));
    }

    @Nullable
    public static String getNbtHash(@Nonnull ItemStack stack) {
        return stack.hasNbt() ? getNBTHash(stack.getNbt()) : null;
    }
}
