package pw.switchcraft.plethora.gameplay;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.ItemStackMetaProvider;
import pw.switchcraft.plethora.gameplay.modules.BindableModuleItem;
import pw.switchcraft.plethora.gameplay.modules.ModuleContextHelpers;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class BindableModuleItemMeta extends ItemStackMetaProvider<BindableModuleItem> {
    public BindableModuleItemMeta() {
        super(BindableModuleItem.class);
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack, @Nonnull BindableModuleItem item) {
        Map<String, Object> result = new HashMap<>();

        GameProfile profile = ModuleContextHelpers.getProfile(stack);
        if (profile != null) {
            Map<String, Object> bound = new HashMap<>();
            result.put("bound", bound);

            if (profile.getId() != null) bound.put("id", profile.getId().toString());
            bound.put("name", profile.getName());
        }

        return result;
    }
}
