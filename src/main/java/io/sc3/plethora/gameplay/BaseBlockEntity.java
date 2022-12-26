package io.sc3.plethora.gameplay;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import io.sc3.plethora.gameplay.registry.Registration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class BaseBlockEntity extends BlockEntity {
    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void unload() {}

    /** Called when the block is broken. */
    public void broken() {
        unload();
    }

    /** Called by a {@link ServerBlockEntityEvents#BLOCK_ENTITY_LOAD} event hook in {@link Registration}. */
    public void onChunkLoaded() {}

    /** Called by a {@link ServerBlockEntityEvents#BLOCK_ENTITY_UNLOAD} event hook in {@link Registration}. */
    public void onChunkUnloaded() {
        unload();
    }

    @Override
    public void markRemoved() {
        unload();
        super.markRemoved();
    }

    /**
     * Called to save data for the client
     *
     * @param nbt The data to send
     */
    protected void writeDescription(NbtCompound nbt) {}

    /**
     * Read data from the canvas
     *
     * @param nbt The data to read
     */
    protected void readDescription(NbtCompound nbt) {}

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeDescription(nbt);
        return nbt;
    }

    @Nonnull
    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    /**
     * Improvement over {@link #markDirty()}
     */
    public void markForUpdate() {
        markDirty();
        World world = Objects.requireNonNull(getWorld());
        BlockPos pos = getPos();
        BlockState state = world.getBlockState(pos);
        world.updateListeners(getPos(), state, state, Block.NOTIFY_ALL);
        world.updateNeighborsAlways(pos, state.getBlock());
    }
}
