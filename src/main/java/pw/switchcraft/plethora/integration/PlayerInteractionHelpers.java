package pw.switchcraft.plethora.integration;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import pw.switchcraft.plethora.api.method.FutureMethodResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PlayerInteractionHelpers {
    public static FutureMethodResult use(@Nonnull ServerPlayerEntity player, @Nonnull HitResult baseHit,
                                         @Nonnull Hand hand, int duration) {
        ItemStack stack = player.getStackInHand(hand);
        World world = player.getEntityWorld();

        switch (baseHit.getType()) {
            case ENTITY -> {
                EntityHitResult hit = (EntityHitResult) baseHit;
                Entity target = hit.getEntity();

                Vec3d pos = hit.getPos().subtract(target.getPos());

                ActionResult result = target.interactAt(player, pos, hand);
                if (result.isAccepted()) return FutureMethodResult.result(true, "entity");
                if (player.interact(target, hand).isAccepted()) return FutureMethodResult.result(true, "entity");
            }
            case BLOCK -> {
                // When right next to a block the hit direction gets inverted. Try both to see if one works.
                BlockHitResult hit = (BlockHitResult) baseHit;
                BlockPos pos = hit.getBlockPos();
                Direction side = hit.getSide();
                boolean insideBlock = hit.isInsideBlock();

                if (!world.isAir(pos) && world.getWorldBorder().contains(pos)) {
                    Vec3d hitPos = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());

                    ActionResult result = rightClickBlock(player, world, stack, hand, pos, side, hitPos, insideBlock);
                    if (result.isAccepted()) return FutureMethodResult.result(true, "block");

                    result = rightClickBlock(player, world, stack, hand, pos, side.getOpposite(), hitPos, insideBlock);
                    if (result.isAccepted()) return FutureMethodResult.result(true, "block");
                }
            }
        }

        // TODO: if (stack.isEmpty() && baseHit.getType() == MISS) onEmptyClick
        if (!stack.isEmpty()) {
            ActionResult result = player.interactionManager.interactItem(player, world, stack, hand);
            if (result.isAccepted()) {
                ItemStack active = player.getActiveItem();

                if (!active.isEmpty()) {
                    return FutureMethodResult.delayed(duration, () -> {
                        // If we're still holding this item, it's still there and we haven't started using something
                        // else.
                        if (player.getStackInHand(hand).equals(active) && !active.isEmpty() &&
                            (player.getActiveItem().equals(active) || player.getActiveItem().isEmpty())) {
                            // Then stop it!
                            active.onStoppedUsing(world, player, active.getMaxUseTime() - duration);

                            player.clearActiveItem();
                            return FutureMethodResult.result(true, "item");
                        } else {
                            return FutureMethodResult.result(false);
                        }
                    });
                } else {
                    return FutureMethodResult.result(true, "item");
                }
            }
        }

        return FutureMethodResult.result(false);
    }

    /**
     * Modified version of {@link ServerPlayerInteractionManager#interactBlock} with creative checks and advancement
     * criteria removed.
     */
    private static ActionResult rightClickBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
                                                BlockPos pos, Direction side, Vec3d hitPos, boolean insideBlock) {
        BlockState blockState = world.getBlockState(pos);
        BlockHitResult hitResult = new BlockHitResult(hitPos, side, pos, insideBlock);

        boolean bypass = player.shouldCancelInteraction()
            && (!player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty());
        if (!bypass) {
            ActionResult outResult = blockState.onUse(world, player, hand, hitResult);
            if (outResult.isAccepted()) {
                return outResult;
            }
        }

        if (!stack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, hitResult);
            // Plethora: Removed isCreative check here
            return stack.useOnBlock(itemUsageContext);
        } else {
            return ActionResult.PASS;
        }
    }

    /**
     * Attack an entity with a player
     *
     * @param player    The player who is attacking
     * @param hitEntity The entity which was attacked
     * @return If this entity could be attacked.
     */
    public static Pair<Boolean, String> attack(@Nonnull ServerPlayerEntity player, @Nullable Entity hitEntity) {
        if (hitEntity != null) {
            // TODO: Use the original entity for the main attacker
            player.attack(hitEntity);
            return new Pair<>(true, "entity");
        }

        return new Pair<>(false, "Nothing to attack here");
    }
}
