package pw.switchcraft.plethora.gameplay.redstone;

import dan200.computercraft.shared.common.IBundledRedstoneBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.BaseBlockWithEntity;
import pw.switchcraft.plethora.gameplay.registry.Registration;

import javax.annotation.Nullable;

public class RedstoneIntegratorBlock extends BaseBlockWithEntity implements IBundledRedstoneBlock {
    public RedstoneIntegratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
        RedstoneIntegratorBlockEntity integrator = getIntegrator(world, pos);
        return integrator != null ? integrator.getRedstoneOutput(side.getOpposite()) : 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getStrongRedstonePower(state, world, pos, direction); // Weak same as strong
    }

    @Override
    public boolean getBundledRedstoneConnectivity(World world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public int getBundledRedstoneOutput(World world, BlockPos pos, Direction side) {
        RedstoneIntegratorBlockEntity integrator = getIntegrator(world, pos);
        return integrator != null ? integrator.getBundledRedstoneOutput(side) : 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        RedstoneIntegratorBlockEntity integrator = getIntegrator(world, pos);
        if (integrator != null) integrator.updateInput();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneIntegratorBlockEntity(Registration.ModBlockEntities.REDSTONE_INTEGRATOR, pos, state);
    }

    private RedstoneIntegratorBlockEntity getIntegrator(BlockView world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof RedstoneIntegratorBlockEntity integrator ? integrator : null;
    }



    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
