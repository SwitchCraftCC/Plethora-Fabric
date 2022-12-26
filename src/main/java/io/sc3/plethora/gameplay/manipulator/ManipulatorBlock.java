package io.sc3.plethora.gameplay.manipulator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import io.sc3.plethora.gameplay.BaseBlockWithEntity;
import io.sc3.plethora.util.PlayerHelpers;

import javax.annotation.Nullable;

import static io.sc3.plethora.gameplay.manipulator.ManipulatorType.MARK_1;
import static io.sc3.plethora.gameplay.registry.Registration.ModBlockEntities.MANIPULATOR_MARK_1;
import static io.sc3.plethora.gameplay.registry.Registration.ModBlockEntities.MANIPULATOR_MARK_2;

public class ManipulatorBlock extends BaseBlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;

    public static final double OFFSET = 10.0 / 16.0;
    public static final double PIX = 1 / 16.0;
    public static final double BOX_EXPAND = 0.002;

    private final ManipulatorType type;

    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 10, 16);
    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0, 6, 0, 16, 16, 16);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 10);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 6, 16, 16, 16);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 0, 0, 10, 16, 16);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(6, 0, 0, 16, 16, 16);

    public ManipulatorBlock(Settings settings, ManipulatorType type) {
        super(settings);

        this.type = type;

        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> properties) {
        properties.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getSide().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ManipulatorBlockEntity(type == MARK_1 ? MANIPULATOR_MARK_1 : MANIPULATOR_MARK_2, pos, state, type);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ManipulatorBlockEntity manipulator) {
            manipulator.setOwningProfile(PlayerHelpers.getProfile(placer));
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(
            type,
            this.type == MARK_1 ? MANIPULATOR_MARK_1 : MANIPULATOR_MARK_2,
            ManipulatorBlockEntity::tick
        );
    }

    public ManipulatorType getType() {
        return type;
    }
}
