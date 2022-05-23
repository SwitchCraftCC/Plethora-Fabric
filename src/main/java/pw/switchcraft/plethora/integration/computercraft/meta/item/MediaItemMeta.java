package pw.switchcraft.plethora.integration.computercraft.meta.item;

import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.MediaProviders;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.meta.BasicMetaProvider;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MediaItemMeta extends BasicMetaProvider<ItemStack> {
    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull ItemStack stack) {
        IMedia media = MediaProviders.get(stack);
        if (media == null) return Collections.emptyMap();

        Map<String, Object> out = new HashMap<>(3);
        out.put("label", media.getLabel(stack));
        out.put("recordTitle", media.getAudioTitle(stack));

        SoundEvent soundEvent = media.getAudio(stack);
        if (soundEvent != null) {
            Identifier id = soundEvent.getId();
            if (id != null) out.put("recordName", id.toString());
        }

        return Collections.singletonMap("media", out);
    }
}
