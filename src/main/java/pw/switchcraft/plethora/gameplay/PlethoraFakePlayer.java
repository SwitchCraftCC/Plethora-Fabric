package pw.switchcraft.plethora.gameplay;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.api.Constants;
import pw.switchcraft.plethora.mixin.EntityAccessor;
import pw.switchcraft.plethora.mixin.ServerPlayerInteractionManagerAccessor;
import pw.switchcraft.plethora.util.FakePlayer;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;

import static net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.START_DESTROY_BLOCK;

public class PlethoraFakePlayer extends FakePlayer {
    public static final GameProfile PROFILE = new GameProfile(Constants.FAKEPLAYER_UUID, "[" + Plethora.MOD_ID + "]");

    private final WeakReference<Entity> owner;

    private BlockPos digPosition;
    private Block digBlock;

    private int currentDamage = -1;
    private int currentDamageState = -1;

    public PlethoraFakePlayer(ServerWorld world, Entity owner, GameProfile profile) {
        super(world, profile != null && profile.isComplete() ? profile : PROFILE);
        if (owner != null) {
            setCustomName(owner.getName());
            this.owner = new WeakReference<>(owner);
        } else {
            this.owner = null;
        }

        ((EntityAccessor) this).setStandingEyeHeight(0.0f);
    }

    @Nonnull
    @Override
    protected HoverEvent getHoverEvent() {
        Entity owner = getOwner();
        if (owner != null) {
            return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(
                owner.getType(),
                owner.getUuid(),
                owner.getName()
            ));
        } else {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new LiteralText("PlethoraFakePlayer - No owner!"));
        }
    }

    public Entity getOwner() {
        return owner == null ? null : owner.get();
    }

    @Override
    public float getPitch(float tickDelta) {
        // No pitch lerping
        return this.getPitch();
    }

    @Override
    public float getYaw(float tickDelta) {
        // No yaw lerping, and don't use head yaw like livingEntity does
        return this.getYaw();
    }

    @Override
    public double getEyeY() {
        return this.getY();
    }

    @Override
    public float getEyeHeight(EntityPose pose) {
        return 0.0f;
    }

    @Override
    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.0f;
    }

    public void updateCooldown() {
        handSwingTicks = 20;
    }

    private void setState(Block block, BlockPos pos) {
        ServerPlayerInteractionManagerAccessor spim = (ServerPlayerInteractionManagerAccessor) interactionManager;

        spim.setMining(false);
        spim.setBlockBreakingProgress(-1);

        digPosition = pos;
        digBlock = block;
        currentDamage = -1;
        currentDamageState = -1;
    }

    public Pair<Boolean, String> dig(BlockPos pos, Direction side) {
        World world = getEntityWorld();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();

        int topY = world.getTopY();

        if (block != digBlock || !pos.equals(digPosition)) setState(block, pos);

        if (!world.isAir(pos) && !material.isLiquid()) {
            if (block == Blocks.BEDROCK || state.getHardness(world, pos) <= -1) {
                return new Pair<>(false, "Unbreakable block detected");
            }

            ServerPlayerInteractionManagerAccessor spim = (ServerPlayerInteractionManagerAccessor) interactionManager;
            for (int i = 0; i < 10; i++) {
                if (currentDamageState == -1) {
                    interactionManager.processBlockBreakingAction(pos, START_DESTROY_BLOCK, side, topY);
                    currentDamageState = spim.getBlockBreakingProgress();
                } else {
                    currentDamage++;
                    float hardness = state.calcBlockBreakingDelta(this, world, pos) * (currentDamage + 1);
                    int hardnessState = (int) (hardness * 10);

                    if (hardnessState != currentDamageState) {
                        world.setBlockBreakingInfo(getId(), pos, hardnessState);
                        currentDamageState = hardnessState;
                    }

                    if (hardness >= 1) {
                        interactionManager.tryBreakBlock(pos);

                        setState(null, null);
                        break;
                    }
                }
            }

            return new Pair<>(true, "block");
        }

        return new Pair<>(false, "Nothing to dig here");
    }
}
