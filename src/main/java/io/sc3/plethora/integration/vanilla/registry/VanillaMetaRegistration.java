package io.sc3.plethora.integration.vanilla.registry;

import io.sc3.plethora.api.meta.IMetaProvider;
import io.sc3.plethora.api.meta.IMetaRegistry;
import io.sc3.plethora.api.reference.BlockReference;
import io.sc3.plethora.integration.vanilla.meta.block.BlockMeta;
import io.sc3.plethora.integration.vanilla.meta.block.BlockReferenceMeta;
import io.sc3.plethora.integration.vanilla.meta.block.BlockStateMeta;
import io.sc3.plethora.integration.vanilla.meta.entity.EntityMeta;
import io.sc3.plethora.integration.vanilla.meta.entity.EntityMetaProviders;
import io.sc3.plethora.integration.vanilla.meta.entity.LivingEntityMeta;
import io.sc3.plethora.integration.vanilla.meta.entity.PlayerEntityMeta;
import io.sc3.plethora.integration.vanilla.meta.item.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class VanillaMetaRegistration {
    public static void registerMetaProviders(IMetaRegistry r) {
        // integration.vanilla.block
        provider(r, "block", Block.class, new BlockMeta());
        provider(r, "blockReference", BlockReference.class, new BlockReferenceMeta());
        provider(r, "blockState", BlockState.class, new BlockStateMeta());

        // integration.vanilla.entity
        provider(r, "entity", Entity.class, new EntityMeta());
        provider(r, "itemEntity", ItemEntity.class, EntityMetaProviders.ITEM_ENTITY);
        provider(r, "livingEntity", LivingEntity.class, new LivingEntityMeta());
        provider(r, "playerEntity", PlayerEntity.class, new PlayerEntityMeta());
        provider(r, "sheepEntity", SheepEntity.class, EntityMetaProviders.SHEEP_ENTITY);

        // integration.vanilla.item
        provider(r, "basicItem", ItemStack.class, new BasicItemMeta());
        provider(r, "armorItem", ItemStack.class, new ArmorItemMeta());
        provider(r, "bannerItem", ItemStack.class, new BannerItemMeta());
        provider(r, "itemMaterial", ItemStack.class, new ItemMaterialMeta());
        provider(r, "foodItem", ItemStack.class, ItemMetaProviders.ITEM_FOOD);
        provider(r, "potionItem", ItemStack.class, new PotionItemMeta());
        provider(r, "enchantedItem", ItemStack.class, new EnchantedItemMeta());
    }

    private static <T> void provider(IMetaRegistry r, String name, Class<T> target, IMetaProvider<T> provider) {
        r.registerMetaProvider(Identifier.DEFAULT_NAMESPACE + ":" + name, Identifier.DEFAULT_NAMESPACE, target, provider);
    }
}
