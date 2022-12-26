package io.sc3.plethora.gameplay.modules.kinetic;

import dan200.computercraft.api.turtle.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import io.sc3.plethora.api.IPlayerOwnable;
import io.sc3.plethora.api.module.IModuleHandler;
import io.sc3.plethora.core.TurtleUpgradeModule;
import io.sc3.plethora.gameplay.PlethoraFakePlayer;
import io.sc3.plethora.integration.PlayerInteractionHelpers;
import io.sc3.plethora.integration.computercraft.TurtleFakePlayerProvider;
import io.sc3.plethora.util.PlayerHelpers;

import javax.annotation.Nonnull;

public class KineticTurtleUpgrade extends TurtleUpgradeModule {
    public KineticTurtleUpgrade(@Nonnull ItemStack stack, @Nonnull IModuleHandler handler, @Nonnull String adjective) {
        super(stack, handler, adjective);
    }

    @Nonnull
    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.BOTH;
    }

    @Nonnull
    @Override
    public TurtleCommandResult useTool(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side, @Nonnull TurtleVerb verb, @Nonnull Direction direction) {
        // TODO: Check module blacklist

        IPlayerOwnable ownable = new TurtlePlayerOwnable(turtle);
        PlethoraFakePlayer fakePlayer = TurtleFakePlayerProvider.getPlayer(turtle, ownable);

        // Sync the turtle's inventory with the fake player
        TurtleFakePlayerProvider.load(fakePlayer, turtle, direction);

        try {
            HitResult baseHit = PlayerHelpers.raycast(fakePlayer, 1.5f);

            if (verb == TurtleVerb.DIG && baseHit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult hit = (BlockHitResult) baseHit;
                Pair<Boolean, String> previous = null;

                // We dig multiple times to make up for the delay that turtle.dig results in
                for (int i = 0; i < 4; i++) {
                    Pair<Boolean, String> result = fakePlayer.dig(hit.getBlockPos(), hit.getSide());
                    if (result.getLeft()) {
                        previous = result;
                    } else {
                        return previous != null ? toResult(previous) : toResult(result);
                    }
                }

                return toResult(previous);
            } else if (verb == TurtleVerb.ATTACK && baseHit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult hit = (EntityHitResult) baseHit;
                return toResult(PlayerInteractionHelpers.attack(fakePlayer, hit.getEntity()));
            } else {
                return TurtleCommandResult.failure("Nothing to do here");
            }
        } finally {
            fakePlayer.clearActiveItem();

            TurtleFakePlayerProvider.unload(fakePlayer, turtle);
            fakePlayer.updateCooldown();
        }
    }

    private static TurtleCommandResult toResult(Pair<Boolean, String> result) {
        return result.getLeft()
            ? TurtleCommandResult.success(new Object[]{ result.getRight() })
            : TurtleCommandResult.failure(result.getRight());
    }
}
