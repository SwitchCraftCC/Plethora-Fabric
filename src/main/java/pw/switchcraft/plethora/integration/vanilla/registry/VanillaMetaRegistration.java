package pw.switchcraft.plethora.integration.vanilla.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.meta.IMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.api.reference.BlockReference;
import pw.switchcraft.plethora.integration.vanilla.block.BlockMeta;
import pw.switchcraft.plethora.integration.vanilla.block.BlockReferenceMeta;
import pw.switchcraft.plethora.integration.vanilla.block.BlockStateMeta;
import pw.switchcraft.plethora.integration.vanilla.entity.EntityMetaProviders;
import pw.switchcraft.plethora.integration.vanilla.item.BasicItemMeta;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

public class VanillaMetaRegistration {
    public static void registerMetaProviders(IMetaRegistry r) {
        // integration.vanilla.block
        provider(r, "block", Block.class, new BlockMeta());
        provider(r, "blockReference", BlockReference.class, new BlockReferenceMeta());
        provider(r, "blockState", BlockState.class, new BlockStateMeta());

        // integration.vanilla.entity
        provider(r, "itemEntity", ItemEntity.class, EntityMetaProviders.ITEM_ENTITY);
        provider(r, "sheepEntity", SheepEntity.class, EntityMetaProviders.SHEEP_ENTITY);

        // integration.vanilla.item
        provider(r, "basicItem", ItemStack.class, new BasicItemMeta());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(name, MOD_ID, target, provider);
    }
}
