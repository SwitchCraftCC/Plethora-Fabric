package io.sc3.plethora.api.reference;

import com.google.common.base.Objects;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import io.sc3.plethora.api.IWorldLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class BlockReference implements ConstantReference<BlockReference> {
    private final IWorldLocation location;
    private final WeakReference<BlockEntity> blockEntity;
    private final int beHash;
    private final Direction side;
    private BlockState state;
    private boolean valid = true;

    public BlockReference(@Nonnull IWorldLocation location, @Nonnull BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side) {
        this.location = location;
        this.blockEntity = blockEntity == null ? null : new WeakReference<>(blockEntity);
        beHash = blockEntity == null ? 0 : blockEntity.hashCode();
        this.side = side;
        this.state = state;
    }

    public BlockReference(@Nonnull IWorldLocation location, @Nonnull BlockState state, @Nullable BlockEntity blockEntity) {
        this(location, state, blockEntity, null);
    }

    public BlockReference(@Nonnull IWorldLocation location, @Nullable Direction side) {
        this(
            location,
            location.getWorld().getBlockState(location.getPos()),
            location.getWorld().getBlockEntity(location.getPos()),
            side
        );
    }

    public BlockReference(@Nonnull IWorldLocation location) {
        this(location, null);
    }

    @Nonnull
    @Override
    public BlockReference get() throws LuaException {
        World world = location.getWorld();
        BlockPos pos = location.getPos();

        BlockState newState = world.getBlockState(pos);
        BlockEntity newBe = world.getBlockEntity(pos);

        if (blockEntity == null) {
            // We only monitor block changes if we can't compare the TE
            if (state.getBlock() != newState.getBlock()) {
                valid = false;
                throw new LuaException("The block is no longer there");
            }

            if (newBe != null) {
                valid = false;
                throw new LuaException("The block has changed");
            }
        } else {
            BlockEntity oldBe = blockEntity.get();
            if (oldBe == null) {
                valid = false;
                throw new LuaException("The block is no longer there");
            } else if (!oldBe.equals(newBe)) {
                valid = false;
                throw new LuaException("The block has changed");
            }
        }

        // Update the block state if everything is OK
        state = world.getBlockState(pos);

        valid = true;
        return this;
    }

    @Nonnull
    @Override
    public BlockReference safeGet() throws LuaException {
        if (!valid) throw new LuaException("The block has changed");

        if (blockEntity != null) {
            BlockEntity oldBe = blockEntity.get();
            if (oldBe == null || oldBe.isRemoved()) throw new LuaException("The block has changed");
        }

        return this;
    }

    @Nonnull
    public IWorldLocation getLocation() {
        return location;
    }

    @Nonnull
    public BlockState getState() {
        return state;
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        return blockEntity == null ? null : blockEntity.get();
    }

    @Nullable
    public Direction getSide() {
        return side;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockReference that = (BlockReference) o;

        if (!location.equals(that.location) || beHash != that.beHash) return false;

        if (blockEntity != that.blockEntity) {
            if (blockEntity == null) return false;

            BlockEntity thisBe = blockEntity.get();
            BlockEntity thatBe = that.blockEntity == null ? null : that.blockEntity.get();

            if (!Objects.equal(thisBe, thatBe)) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return location.hashCode() + 31 * beHash;
    }
}
