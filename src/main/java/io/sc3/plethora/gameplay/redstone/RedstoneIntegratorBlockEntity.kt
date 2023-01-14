package io.sc3.plethora.gameplay.redstone;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.impl.BundledRedstone;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import io.sc3.plethora.gameplay.BaseBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.sc3.plethora.Plethora.log;

public class RedstoneIntegratorBlockEntity extends BaseBlockEntity {
    final byte[] inputs = new byte[6];
    final byte[] outputs = new byte[6];
    final int[] bundledInputs = new int[6];
    final int[] bundledOutputs = new int[6];

    private boolean outputDirty = false;
    private boolean inputDirty = false;

    final Set<IComputerAccess> computers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public RedstoneIntegratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    void updateInput() {
        World world = getWorld();
        if (world == null || world.isClient || isRemoved() || !world.isChunkLoaded(pos)) return;

        boolean changed = false;
        for (Direction dir : Direction.values()) {
            BlockPos offset = pos.offset(dir);
            Direction offsetSide = dir.getOpposite();
            int dirIdx = dir.ordinal();

            byte newInput = (byte) getRedstoneInput(world, offset, offsetSide);
            if (newInput != inputs[dirIdx]) {
                inputs[dirIdx] = newInput;
                changed = true;
            }

            short newBundled = (short) BundledRedstone.getOutput(world, offset, offsetSide);
            if (bundledInputs[dirIdx] != newBundled) {
                bundledInputs[dirIdx] = newBundled;
                changed = true;
            }
        }

        if (changed) enqueueInputTick();
    }

    void updateOnce() {
        World world = getWorld();
        if (world == null || world.isClient || isRemoved() || !world.isChunkLoaded(pos)) return;

        if (outputDirty) {
            for (Direction dir : Direction.values()) {
                propagateRedstoneOutput(world, pos, dir);
            }
            outputDirty = false;
        }

        if (inputDirty) {
            Iterator<IComputerAccess> computers = this.computers.iterator();
            while (computers.hasNext()) {
                IComputerAccess computer = computers.next();
                try {
                    computer.queueEvent("redstone", computer.getAttachmentName());
                } catch (RuntimeException e) {
                    log.error("Could not queue redstone event", e);
                    computers.remove();
                }
            }
            inputDirty = false;
        }
    }

    void enqueueInputTick() {
        if (!inputDirty) {
            inputDirty = true;
            RedstoneIntegratorTicker.enqueueTick(this);
        }
    }

    void enqueueOutputTick() {
        if (!outputDirty) {
            outputDirty = true;
            RedstoneIntegratorTicker.enqueueTick(this);
        }
    }

    @Override
    public void onChunkLoaded() {
        super.onChunkLoaded();

        // Update the output to ensure all redstone is turned off.
        enqueueOutputTick();
    }

    /**
     * Gets the redstone input for an adjacent block
     *
     * @param world The world we exist in
     * @param pos   The position of the neighbour
     * @param side  The side we are reading from
     * @return The effective redstone power
     * @see net.minecraft.block.AbstractRedstoneGateBlock#getInputLevel(WorldView, BlockPos, Direction)
     */
    private static int getRedstoneInput(World world, BlockPos pos, Direction side) {
        int power = world.getEmittedRedstonePower(pos, side);
        if (power >= 15) return power;

        BlockState neighbour = world.getBlockState(pos);
        return neighbour.getBlock() == Blocks.REDSTONE_WIRE
            ? Math.max(power, neighbour.get(RedstoneWireBlock.POWER))
            : power;
    }

    /**
     * Propagate ordinary output
     *
     * @param world The world we exist in
     * @param pos   Our position
     * @param side  The side to propagate to
     * @see net.minecraft.block.AbstractRedstoneGateBlock#updateTarget(World, BlockPos, BlockState)
     */
    private static void propagateRedstoneOutput(World world, BlockPos pos, Direction side) {
        BlockState block = world.getBlockState(pos);
        BlockPos neighbourPos = pos.offset(side);
        world.updateNeighbor(neighbourPos, block.getBlock(), pos);
        world.updateNeighborsExcept(neighbourPos, block.getBlock(), side.getOpposite());
    }

    public int getRedstoneOutput(Direction side) {
        return outputs[side.ordinal()];
    }

    public int getBundledRedstoneOutput(@Nonnull Direction side) {
        return bundledOutputs[side.ordinal()];
    }

    @Nullable
    public IPeripheral getPeripheral(@Nonnull Direction side) {
        return new RedstoneIntegratorPeripheral(this);
    }
}
