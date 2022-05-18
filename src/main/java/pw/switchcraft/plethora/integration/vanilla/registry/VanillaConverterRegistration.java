package pw.switchcraft.plethora.integration.vanilla.registry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.converter.IConverter;
import pw.switchcraft.plethora.api.converter.IConverterRegistry;
import pw.switchcraft.plethora.api.reference.BlockReference;

import static pw.switchcraft.plethora.integration.vanilla.converter.VanillaConverters.*;

public class VanillaConverterRegistration {
    public static void registerConverters(IConverterRegistry r) {
        converter(r, "getStackItem", ItemStack.class, GET_STACK_ITEM);
        converter(r, "getBlockReferenceBlock", BlockReference.class, GET_BLOCK_REFERENCE_BLOCK);
        converter(r, "getBlockReferenceBlockEntity", BlockReference.class, GET_BLOCK_REFERENCE_BLOCK_ENTITY);
        converter(r, "getBlockEntityReference", BlockEntity.class, GET_BLOCK_ENTITY_REFERENCE);
        converter(r, "getBlockStateBlock", BlockState.class, GET_BLOCK_STATE_BLOCK);
        converter(r, "getEntityIdentifier", Entity.class, GET_ENTITY_IDENTIFIER);
    }

    private static <T> void converter(IConverterRegistry r, String name, Class<T> target, IConverter<T, ?> provider) {
        r.registerConverter(name, "minecraft", target, provider);
    }
}
