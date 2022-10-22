package pw.switchcraft.plethora.mixin.computercraft;

import dan200.computercraft.shared.peripheral.generic.data.ItemData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.switchcraft.plethora.core.ContextFactory;
import pw.switchcraft.plethora.core.executor.BasicExecutor;
import pw.switchcraft.plethora.integration.MetaWrapper;

import java.util.Map;
import java.util.Set;

import static pw.switchcraft.plethora.Plethora.log;

@Mixin(ItemData.class)
public class ItemDataMixin {
    // TODO: Probably better eventually to replace BasicItemMeta with ItemData.fill, though it would conflict with this
    //       mixin
    private static final Set<String> IGNORE_KEYS = Set.of("name", "displayName", "damage", "maxDamage",
        "count", "maxCount", "nbtHash", "durability", "lore");

    // TODO: PR an ItemData API to CC:T/CC:R
    @Inject(
        method = "fill(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)Ljava/util/Map;",
        at = @At("RETURN")
    )
    private static <T extends Map<? super String, Object>> void fill(
        T data,
        ItemStack stack,
        CallbackInfoReturnable<T> cir
    ) {
        if (stack.isEmpty()) return; // Let CC handle this

        // Supply Plethora's item meta to CC items
        try {
            MetaWrapper<ItemStack> wrapper = MetaWrapper.of(stack.copy());
            Map<String, ?> meta = ContextFactory.of(wrapper).withExecutor(BasicExecutor.INSTANCE).getBaked().getMeta();

            for (Map.Entry<String, ?> entry : meta.entrySet()) {
                // Don't overwrite CC's data or add our own similarly-named keys. CC gets the final say on what it wants
                // to do with these keys to ensure compatibility.
                if (IGNORE_KEYS.contains(entry.getKey())) continue;
                data.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            log.error("Could not supply item meta to CC", e);
        }
    }
}
