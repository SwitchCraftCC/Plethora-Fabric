package io.sc3.plethora.gameplay

import com.mojang.authlib.GameProfile
import io.sc3.plethora.Plethora.modId
import io.sc3.plethora.api.Constants.FAKEPLAYER_UUID
import io.sc3.plethora.mixin.EntityAccessor
import io.sc3.plethora.mixin.ServerPlayerInteractionManagerAccessor
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityPose
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.START_DESTROY_BLOCK
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.HoverEvent
import net.minecraft.text.HoverEvent.Action.SHOW_ENTITY
import net.minecraft.text.HoverEvent.Action.SHOW_TEXT
import net.minecraft.text.Text
import net.minecraft.util.Pair
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.lang.ref.WeakReference

class PlethoraFakePlayer(
  world: ServerWorld,
  ownerEntity: Entity?,
  profile: GameProfile?
) : FakePlayer(
  world,
  if (profile != null && profile.isComplete) profile else PROFILE
) {
  private val owner: WeakReference<Entity>? = ownerEntity
    ?.also { customName = it.name }
    ?.let { WeakReference(it) }

  private var digPosition: BlockPos? = null
  private var digBlock: Block? = null

  private var currentDamage = -1
  private var currentDamageState = -1

  init {
    (this as EntityAccessor).setStandingEyeHeight(0.0f)
  }

  override fun getHoverEvent(): HoverEvent = owner?.get()
    ?.let { HoverEvent(SHOW_ENTITY, HoverEvent.EntityContent(it.type, it.uuid, it.name)) }
    ?: HoverEvent(SHOW_TEXT, Text.of("PlethoraFakePlayer - No owner!"))

  override fun getPitch(tickDelta: Float): Float = pitch // No pitch lerping
  override fun getYaw(tickDelta: Float): Float = yaw // No yaw lerping, and don't use head yaw like livingEntity does
  override fun getEyeY() = y
  override fun getEyeHeight(pose: EntityPose) = 0.0f
  override fun getActiveEyeHeight(pose: EntityPose, dimensions: EntityDimensions) = 0.0f

  fun updateCooldown() {
    handSwingTicks = 20
  }

  private fun setState(block: Block?, pos: BlockPos?) {
    val spim = interactionManager as ServerPlayerInteractionManagerAccessor
    spim.setMining(false)
    spim.blockBreakingProgress = -1

    digPosition = pos
    digBlock = block
    currentDamage = -1
    currentDamageState = -1
  }

  fun dig(pos: BlockPos, side: Direction?): Pair<Boolean, String> {
    val world = entityWorld
    val state = world.getBlockState(pos)
    val block = state.block
    val material = state.material

    val topY = world.topY

    if (block !== digBlock || pos != digPosition) setState(block, pos)

    if (!world.isAir(pos) && !material.isLiquid) {
      if (block === Blocks.BEDROCK || state.getHardness(world, pos) <= -1) {
        return Pair(false, "Unbreakable block detected")
      }

      val spim = interactionManager as ServerPlayerInteractionManagerAccessor
      for (i in 0..9) {
        if (currentDamageState == -1) {
          // TODO: block breaking is now sequenced. may need to increment the sequence each time
          interactionManager.processBlockBreakingAction(pos, START_DESTROY_BLOCK, side, topY, 0)
          currentDamageState = spim.blockBreakingProgress
        } else {
          currentDamage++

          val hardness = state.calcBlockBreakingDelta(this, world, pos) * (currentDamage + 1)
          val hardnessState = (hardness * 10).toInt()
          if (hardnessState != currentDamageState) {
            world.setBlockBreakingInfo(id, pos, hardnessState)
            currentDamageState = hardnessState
          }

          if (hardness >= 1) {
            interactionManager.tryBreakBlock(pos)
            setState(null, null)
            break
          }
        }
      }

      return Pair(true, "block")
    }

    return Pair(false, "Nothing to dig here")
  }

  companion object {
    val PROFILE = GameProfile(FAKEPLAYER_UUID, "[$modId]")
  }
}
