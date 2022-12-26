package pw.switchcraft.plethora.integration.vanilla.converter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pw.switchcraft.plethora.api.WorldLocation;
import pw.switchcraft.plethora.api.converter.ConstantConverter;
import pw.switchcraft.plethora.api.converter.DynamicConverter;
import pw.switchcraft.plethora.api.reference.BlockReference;
import pw.switchcraft.plethora.integration.EntityIdentifier;

public final class VanillaConverters {
    public static final ConstantConverter<ItemStack, Item> GET_STACK_ITEM = ItemStack::getItem;

    public static final DynamicConverter<BlockReference, BlockState> GET_BLOCK_REFERENCE_BLOCK = BlockReference::getState;

    public static final ConstantConverter<BlockReference, BlockEntity> GET_BLOCK_REFERENCE_BLOCK_ENTITY = BlockReference::getBlockEntity;

    public static final ConstantConverter<BlockEntity, BlockReference> GET_BLOCK_ENTITY_REFERENCE = from -> {
        World world = from.getWorld();
        BlockPos pos = from.getPos();

        // Ensure we're referencing a valid TE
        return world == null || pos == null || world.getBlockEntity(pos) != from
            ? null
            : new BlockReference(new WorldLocation(world, pos), world.getBlockState(pos), from);
    };

    public static final ConstantConverter<BlockState, Block> GET_BLOCK_STATE_BLOCK = BlockState::getBlock;

    public static final ConstantConverter<Entity, EntityIdentifier> GET_ENTITY_IDENTIFIER = from ->
        from instanceof PlayerEntity player
            ? new EntityIdentifier.Player(player.getGameProfile())
            : new EntityIdentifier(from);
}
