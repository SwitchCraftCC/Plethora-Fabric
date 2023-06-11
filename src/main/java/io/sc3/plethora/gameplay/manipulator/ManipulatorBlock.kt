package io.sc3.plethora.gameplay.manipulator

import io.sc3.plethora.gameplay.BaseBlockWithEntity
import io.sc3.plethora.gameplay.manipulator.ManipulatorType.MARK_1
import io.sc3.plethora.gameplay.registry.Registration.ModBlockEntities.MANIPULATOR_MARK_1
import io.sc3.plethora.gameplay.registry.Registration.ModBlockEntities.MANIPULATOR_MARK_2
import io.sc3.plethora.util.PlayerHelpers
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

class ManipulatorBlock(settings: Settings?, val type: ManipulatorType) : BaseBlockWithEntity(settings) {
  init {
    defaultState = stateManager.defaultState
      .with(FACING, Direction.DOWN)
  }

  override fun appendProperties(properties: StateManager.Builder<Block, BlockState>) {
    properties.add(FACING)
  }

  override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState
    .with(FACING, ctx.side.opposite)

  override fun createBlockEntity(pos: BlockPos, state: BlockState) = ManipulatorBlockEntity(
    if (type == MARK_1) MANIPULATOR_MARK_1 else MANIPULATOR_MARK_2,
    pos,
    state,
    type
  )

  override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
    super.onPlaced(world, pos, state, placer, itemStack)
    val be = world.getBlockEntity(pos)
    if (be is ManipulatorBlockEntity) {
      be.owningProfile = PlayerHelpers.getProfile(placer)
    }
  }

  override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) =
    when (state.get(FACING)) {
      Direction.DOWN  -> DOWN_SHAPE
      Direction.UP    -> UP_SHAPE
      Direction.NORTH -> NORTH_SHAPE
      Direction.SOUTH -> SOUTH_SHAPE
      Direction.WEST  -> WEST_SHAPE
      Direction.EAST  -> EAST_SHAPE
      null -> DOWN_SHAPE
    }

  override fun <T : BlockEntity> getTicker(
    world: World,
    state: BlockState,
    type: BlockEntityType<T>
  ) : BlockEntityTicker<T>? {
    if (world.isClient) return null
    val be = if (this.type == MARK_1) MANIPULATOR_MARK_1 else MANIPULATOR_MARK_2
    return checkType(type, be, ManipulatorBlockEntity::tick)
  }

  companion object {
    @JvmField
    val FACING: DirectionProperty = Properties.FACING

    const val OFFSET     = 10.0 / 16.0
    const val PIX        = 1 / 16.0
    const val BOX_EXPAND = 0.002

    private val DOWN_SHAPE  = createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0)
    private val UP_SHAPE    = createCuboidShape(0.0, 6.0, 0.0, 16.0, 16.0, 16.0)
    private val NORTH_SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 10.0)
    private val SOUTH_SHAPE = createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 16.0)
    private val WEST_SHAPE  = createCuboidShape(0.0, 0.0, 0.0, 10.0, 16.0, 16.0)
    private val EAST_SHAPE  = createCuboidShape(6.0, 0.0, 0.0, 16.0, 16.0, 16.0)
  }
}
