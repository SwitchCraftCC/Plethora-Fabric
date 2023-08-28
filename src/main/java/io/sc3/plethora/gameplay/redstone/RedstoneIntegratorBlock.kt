package io.sc3.plethora.gameplay.redstone

import dan200.computercraft.shared.common.IBundledRedstoneBlock
import io.sc3.plethora.gameplay.BaseBlockWithEntity
import io.sc3.plethora.gameplay.registry.Registration.ModBlockEntities.REDSTONE_INTEGRATOR
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

class RedstoneIntegratorBlock(settings: Settings) : BaseBlockWithEntity(settings), IBundledRedstoneBlock {
  override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

  override fun emitsRedstonePower(state: BlockState) = true

  override fun getStrongRedstonePower(state: BlockState, world: BlockView, pos: BlockPos, side: Direction) =
    getIntegrator(world, pos)?.getRedstoneOutput(side.opposite) ?: 0

  override fun getWeakRedstonePower(state: BlockState, world: BlockView, pos: BlockPos, direction: Direction) =
    getStrongRedstonePower(state, world, pos, direction) // Weak same as strong

  override fun getBundledRedstoneOutput(world: World, pos: BlockPos, side: Direction) =
    getIntegrator(world, pos)?.getBundledRedstoneOutput(side) ?: 0

  override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos,
                              notify: Boolean) {
    super.neighborUpdate(state, world, pos, block, fromPos, notify)
    getIntegrator(world, pos)?.updateInput()
  }

  override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
    RedstoneIntegratorBlockEntity(REDSTONE_INTEGRATOR, pos, state)

  private fun getIntegrator(world: BlockView, pos: BlockPos): RedstoneIntegratorBlockEntity? {
    val be = world.getBlockEntity(pos)
    return be as? RedstoneIntegratorBlockEntity
  }
}
