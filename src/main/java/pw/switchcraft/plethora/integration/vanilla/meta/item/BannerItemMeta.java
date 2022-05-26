package pw.switchcraft.plethora.integration.vanilla.meta.item;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.api.meta.ItemStackMetaProvider;

import javax.annotation.Nonnull;
import java.util.*;

public final class BannerItemMeta extends ItemStackMetaProvider<BannerItem> {
    public BannerItemMeta() {
        super(BannerItem.class);
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull BannerItem banner) {
        List<Map<String, ?>> out;

        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
        if (nbt != null && nbt.contains("Patterns")) {
            NbtList list = nbt.getList("Patterns", NbtElement.COMPOUND_TYPE);

            out = new ArrayList<>(list.size());
            for (int i = 0; i < list.size() && i < 6; i++) {
                NbtCompound patternNbt = list.getCompound(i);

                DyeColor color = DyeColor.byId(patternNbt.getInt("Color"));
                BannerPattern pattern = getPatternById(patternNbt.getString("Pattern"));

                if (pattern != null) {
                    Map<String, String> entry = new HashMap<>(4);
                    entry.put("id", pattern.getId());
                    entry.put("name", pattern.getId()); // TODO: This has changed

                    entry.put("colour", color.getName());
                    entry.put("color", color.getName());

                    out.add(entry);
                }
            }
        } else {
            out = Collections.emptyList();
        }

        return Collections.singletonMap("banner", out);
    }

    private static BannerPattern getPatternById(String id) {
        for (BannerPattern pattern : Registry.BANNER_PATTERN) {
            if (pattern.getId().equals(id)) return pattern;
        }

        return null;
    }
}
