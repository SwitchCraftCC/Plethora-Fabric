package io.sc3.plethora.gameplay.redstone

import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.impl.BundledRedstone
import io.sc3.plethora.Plethora.log
import io.sc3.plethora.gameplay.BaseBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.RedstoneWireBlock
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.Nonnull
import kotlin.math.max

class RedstoneIntegratorBlockEntity(
  type: BlockEntityType<*>,
  pos: BlockPos,
  state: BlockState
) : BaseBlockEntity(type, pos, state) {
  val inputs = ByteArray(6)
  val outputs = ByteArray(6)
  val bundledInputs = IntArray(6)
  val bundledOutputs = IntArray(6)

  private var outputDirty = false
  private var inputDirty = false

  val computers: MutableSet<IComputerAccess> = Collections.newSetFromMap(ConcurrentHashMap())
  val peripheral = RedstoneIntegratorPeripheral(this)

  fun updateInput() {
    val world = world ?: return
    if (world.isClient || isRemoved || !world.isChunkLoaded(pos)) return

    var changed = false
    Direction.values().forEach { dir ->
      val offset = pos.offset(dir)
      val offsetSide = dir.opposite
      val dirIdx = dir.ordinal

      val newInput = getRedstoneInput(world, offset, offsetSide).toByte()
      if (newInput != inputs[dirIdx]) {
        inputs[dirIdx] = newInput
        changed = true
      }

      val newBundled = BundledRedstone.getOutput(world, offset, offsetSide).toShort()
      if (bundledInputs[dirIdx] != newBundled.toInt()) {
        bundledInputs[dirIdx] = newBundled.toInt()
        changed = true
      }
    }

    if (changed) enqueueInputTick()
  }

  fun updateOnce() {
    val world = world ?: return
    if (world.isClient || isRemoved || !world.isChunkLoaded(pos)) return

    if (outputDirty) {
      for (dir in Direction.values()) {
        propagateRedstoneOutput(world, pos, dir)
      }
      outputDirty = false
    }

    if (inputDirty) {
      val computers = computers.iterator()
      while (computers.hasNext()) {
        val computer = computers.next()
        try {
          computer.queueEvent("redstone", computer.attachmentName)
        } catch (e: RuntimeException) {
          log.error("Could not queue redstone event", e)
          computers.remove()
        }
      }
      inputDirty = false
    }
  }

  fun enqueueInputTick() {
    if (!inputDirty) {
      inputDirty = true
      RedstoneIntegratorTicker.enqueueTick(this)
    }
  }

  fun enqueueOutputTick() {
    if (!outputDirty) {
      outputDirty = true
      RedstoneIntegratorTicker.enqueueTick(this)
    }
  }

  override fun onChunkLoaded() {
    super.onChunkLoaded()

    // Update the output to ensure all redstone is turned off.
    enqueueOutputTick()
  }

  fun getRedstoneOutput(side: Direction): Int {
    return outputs[side.ordinal].toInt()
  }

  fun getBundledRedstoneOutput(@Nonnull side: Direction): Int {
    return bundledOutputs[side.ordinal]
  }

  companion object {
    /**
     * Gets the redstone input for an adjacent block
     *
     * @param world The world we exist in
     * @param pos   The position of the neighbour
     * @param side  The side we are reading from
     * @return The effective redstone power
     * @see net.minecraft.block.AbstractRedstoneGateBlock.getInputLevel
     */
    private fun getRedstoneInput(world: World, pos: BlockPos, side: Direction): Int {
      val power = world.getEmittedRedstonePower(pos, side)
      if (power >= 15) return power

      val neighbour = world.getBlockState(pos)
      return if (neighbour.block === Blocks.REDSTONE_WIRE) {
        max(power, neighbour.get(RedstoneWireBlock.POWER))
      } else {
        power
      }
    }

    /**
     * Propagate ordinary output
     *
     * @param world The world we exist in
     * @param pos   Our position
     * @param side  The side to propagate to
     * @see net.minecraft.block.AbstractRedstoneGateBlock.updateTarget
     */
    private fun propagateRedstoneOutput(world: World, pos: BlockPos, side: Direction) {
      val block = world.getBlockState(pos)
      val neighbourPos = pos.offset(side)
      world.updateNeighbor(neighbourPos, block.block, pos)
      world.updateNeighborsExcept(neighbourPos, block.block, side.opposite)
    }
  }
}
