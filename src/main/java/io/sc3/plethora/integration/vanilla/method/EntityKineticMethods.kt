package io.sc3.plethora.integration.vanilla.method

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.ArgumentExt.optHand
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.SubtargetedModuleMethod
import io.sc3.plethora.gameplay.modules.kinetic.KineticMethods
import io.sc3.plethora.gameplay.registry.PlethoraModules.KINETIC_M
import io.sc3.plethora.integration.PlayerInteractionHelpers
import io.sc3.plethora.util.Helpers.normaliseAngle
import io.sc3.plethora.util.PlayerHelpers
import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import java.util.*

object EntityKineticMethods {
  private val LOOK_FLAGS = EnumSet.of(PositionFlag.X, PositionFlag.Y, PositionFlag.Z)

  val LOOK = SubtargetedModuleMethod.of(
    "look", KINETIC_M, LivingEntity::class.java,
    "function(yaw:number, pitch:number) -- Look in a set direction", ::look
  )
  private fun look(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val entity = KineticMethods.getContext(unbaked).entity

    val yaw = normaliseAngle(args.getFiniteDouble(0)).toFloat()
    val pitch = MathHelper.clamp(normaliseAngle(args.getFiniteDouble(1)), -90.0, 90.0).toFloat()

    if (entity is ServerPlayerEntity) {
      val pos = entity.getPos()
      entity.networkHandler.requestTeleport(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch, LOOK_FLAGS)
    } else {
      entity.yaw = yaw
      entity.setBodyYaw(yaw)
      entity.setHeadYaw(yaw)
      entity.pitch = pitch
    }

    return FutureMethodResult.empty()
  }

  val USE = SubtargetedModuleMethod.of(
    "use", KINETIC_M, LivingEntity::class.java,
    "function([duration:integer], [hand:string]):boolean, string|nil -- Right click with this item using a " +
      "particular hand (\"main\" or \"off\"). The duration is in ticks, or 1/20th of a second.",
    ::use
  )
  private fun use(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val ctx = KineticMethods.getContext(unbaked)
    val playerCtx = KineticMethods.getPlayer(ctx)
    val player = playerCtx.player
    val fakePlayer = playerCtx.fakePlayer

    val duration = args.optInt(0, 0)
    val hand = args.optHand(1)

    return try {
      val hit = PlayerHelpers.raycast(player)
      PlayerInteractionHelpers.use(player, hit, hand, duration)
    } finally {
      player.clearActiveItem()
      fakePlayer?.updateCooldown()
    }
  }

  val SWING = SubtargetedModuleMethod.of(
    "swing", KINETIC_M, LivingEntity::class.java,
    "function():boolean, string|nil -- Left click with the item in the main hand. Returns the action taken."
  ) { unbaked, _ -> swing(unbaked) }
  private fun swing(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val ctx = KineticMethods.getContext(unbaked)
    val playerCtx = KineticMethods.getPlayer(ctx)
    val player = playerCtx.player
    val fakePlayer = playerCtx.fakePlayer

    return try {
      val baseHit = PlayerHelpers.raycast(player)
      when (baseHit.type) {
        HitResult.Type.ENTITY -> {
          val hit = baseHit as EntityHitResult
          val result = PlayerInteractionHelpers.attack(player, hit.entity, hit)
          FutureMethodResult.result(result.left, result.right)
        }
        HitResult.Type.BLOCK -> {
          val hit = baseHit as BlockHitResult
          if (fakePlayer != null) {
            val result = fakePlayer.dig(hit.blockPos, hit.side)
            FutureMethodResult.result(result.left, result.right)
          } else {
            FutureMethodResult.result(false, "Nothing to do here")
          }
        }
        else -> FutureMethodResult.result(false, "Nothing to do here")
      }
    } finally {
      player.clearActiveItem()
      fakePlayer?.updateCooldown()
    }
  }
}
