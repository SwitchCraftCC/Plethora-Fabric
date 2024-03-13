package io.sc3.plethora.integration

import io.sc3.plethora.api.method.FutureMethodResult
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Pair
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

object PlayerInteractionHelpers {
  fun use(player: ServerPlayerEntity, baseHit: HitResult, hand: Hand, duration: Int): FutureMethodResult {
    val stack = player.getStackInHand(hand)
    val world = player.entityWorld

    when (baseHit.type) {
      HitResult.Type.ENTITY -> {
        val hit = baseHit as EntityHitResult
        val target = hit.entity

        val pos = hit.pos.subtract(target.pos)

        val result = target.interactAt(player, pos, hand)
        if (result.isAccepted) return FutureMethodResult.result(true, "entity")
        if (player.interact(target, hand).isAccepted) return FutureMethodResult.result(true, "entity")
      }

      HitResult.Type.BLOCK -> {
        // When right next to a block the hit direction gets inverted. Try both to see if one works.
        val hit = baseHit as BlockHitResult
        val pos = hit.blockPos
        val side = hit.side
        val insideBlock = hit.isInsideBlock

        if (!world.isAir(pos) && world.worldBorder.contains(pos)) {
          var result = rightClickBlock(player, world, stack, hand, pos, side, hit.pos, insideBlock)
          if (result.isAccepted) return FutureMethodResult.result(true, "block")

          result = rightClickBlock(player, world, stack, hand, pos, side.opposite, hit.pos, insideBlock)
          if (result.isAccepted) return FutureMethodResult.result(true, "block")
        }
      }

      else -> {}
    }

    // TODO: if (stack.isEmpty() && baseHit.getType() == MISS) onEmptyClick
    if (!stack.isEmpty) {
      val result = player.interactionManager.interactItem(player, world, stack, hand)
      if (result.isAccepted) {
        val active = player.activeItem
        return if (!active.isEmpty) {
          FutureMethodResult.delayed(duration) {
            // If we're still holding this item, it's still there, and we haven't started using something
            // else.
            if (
              player.getStackInHand(hand) == active && !active.isEmpty &&
              (player.activeItem == active || player.activeItem.isEmpty)
            ) {
              // Then stop it!
              active.onStoppedUsing(world, player, active.maxUseTime - duration)
              player.clearActiveItem()
              return@delayed FutureMethodResult.result(true, "item")
            } else {
              return@delayed FutureMethodResult.result(false)
            }
          }
        } else {
          FutureMethodResult.result(true, "item")
        }
      }
    }

    return FutureMethodResult.result(false)
  }

  /**
   * Modified version of [ServerPlayerInteractionManager.interactBlock] with creative checks and advancement
   * criteria removed.
   */
  private fun rightClickBlock(player: ServerPlayerEntity, world: World, stack: ItemStack, hand: Hand,
                              pos: BlockPos, side: Direction, hitPos: Vec3d, insideBlock: Boolean): ActionResult {
    val blockState = world.getBlockState(pos)
    val hitResult = BlockHitResult(hitPos, side, pos, insideBlock)

    val bypass = (player.shouldCancelInteraction()
        && (!player.mainHandStack.isEmpty || !player.offHandStack.isEmpty))
    if (!bypass) {
      val outResult = blockState.onUse(world, player, hand, hitResult)
      if (outResult.isAccepted) return outResult
    }

    return if (!stack.isEmpty && !player.itemCooldownManager.isCoolingDown(stack.item)) {
      val itemUsageContext = ItemUsageContext(player, hand, hitResult)
      // Plethora: Removed isCreative check here
      stack.useOnBlock(itemUsageContext)
    } else {
      ActionResult.PASS
    }
  }

  /**
   * Attack an entity with a player
   *
   * @param player    The player who is attacking
   * @param hitEntity The entity which was attacked
   * @return If this entity could be attacked.
   */
  @JvmStatic
  fun attack(player: ServerPlayerEntity, hitEntity: Entity?, hitResult: EntityHitResult): Pair<Boolean, String> =
    if (hitEntity != null) {
      // TODO: Use the original entity for the main attacker
      player.attack(hitEntity)
      Pair(true, "entity")
    } else {
      Pair(false, "Nothing to attack here")
    }
}
