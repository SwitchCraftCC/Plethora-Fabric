package pw.switchcraft.plethora.integration.vanilla.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.meta.IMetaProvider;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.api.reference.BlockReference;
import pw.switchcraft.plethora.integration.vanilla.meta.block.BlockMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.block.BlockReferenceMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.block.BlockStateMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.entity.EntityMetaProviders;
import pw.switchcraft.plethora.integration.vanilla.meta.entity.LivingEntityMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.entity.PlayerEntityMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.item.ArmorItemMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.item.BannerItemMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.item.BasicItemMeta;
import pw.switchcraft.plethora.integration.vanilla.meta.item.ItemMaterialMeta;

public class VanillaMetaRegistration {
    public static void registerMetaProviders(IMetaRegistry r) {
        // integration.vanilla.block
        provider(r, "block", Block.class, new BlockMeta());
        provider(r, "blockReference", BlockReference.class, new BlockReferenceMeta());
        provider(r, "blockState", BlockState.class, new BlockStateMeta());

        // integration.vanilla.entity
        provider(r, "itemEntity", ItemEntity.class, EntityMetaProviders.ITEM_ENTITY);
        provider(r, "livingEntity", LivingEntity.class, new LivingEntityMeta());
        provider(r, "playerEntity", PlayerEntity.class, new PlayerEntityMeta());
        provider(r, "sheepEntity", SheepEntity.class, EntityMetaProviders.SHEEP_ENTITY);

        // integration.vanilla.item
        provider(r, "basicItem", ItemStack.class, new BasicItemMeta());
        provider(r, "armorItem", ItemStack.class, new ArmorItemMeta());
        provider(r, "bannerItem", ItemStack.class, new BannerItemMeta());
        provider(r, "itemMaterial", ItemStack.class, new ItemMaterialMeta());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(Identifier.DEFAULT_NAMESPACE + ":" + name, Identifier.DEFAULT_NAMESPACE, target, provider);
    }
}
