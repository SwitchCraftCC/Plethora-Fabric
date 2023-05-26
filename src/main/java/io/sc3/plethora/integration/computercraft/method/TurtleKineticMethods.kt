package io.sc3.plethora.integration.computercraft.method

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.turtle.ITurtleAccess
import io.sc3.plethora.api.IPlayerOwnable
import io.sc3.plethora.api.method.ContextKeys.ORIGIN
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IContext
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.SubtargetedModuleMethod
import io.sc3.plethora.core.ContextHelpers
import io.sc3.plethora.gameplay.registry.PlethoraModules.KINETIC_M
import io.sc3.plethora.integration.PlayerInteractionHelpers
import io.sc3.plethora.integration.computercraft.TurtleFakePlayerProvider
import io.sc3.plethora.util.PlayerHelpers
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult

object TurtleKineticMethods {
  val USE = SubtargetedModuleMethod.of(
    "use", KINETIC_M, ITurtleAccess::class.java,
    "function([duration:integer]):boolean, string|nil -- Right click with this item. The duration is in ticks, " +
      "or 1/20th of a second.",
    ::use
  )
  private fun use(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val (_, turtle, ownable) = getContext(unbaked)
    val fakePlayer = TurtleFakePlayerProvider.getPlayer(turtle, ownable)

    val duration = args.optInt(0, 0)

    // Sync the turtle's inventory with the fake player
    TurtleFakePlayerProvider.load(fakePlayer, turtle, turtle.direction)

    return try {
      val hit = PlayerHelpers.raycast(fakePlayer, 1.5f)
      PlayerInteractionHelpers.use(fakePlayer, hit, Hand.MAIN_HAND, duration)
    } finally {
      TurtleFakePlayerProvider.unload(fakePlayer, turtle)
      fakePlayer.updateCooldown()
    }
  }

  val SWING = SubtargetedModuleMethod.of(
    "swing", KINETIC_M, ITurtleAccess::class.java,
    "function():boolean, string|nil -- Left click with this item. Returns the action taken."
  ) { unbaked, _ -> swing(unbaked) }
  private fun swing(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val (_, turtle, ownable) = getContext(unbaked)
    val fakePlayer = TurtleFakePlayerProvider.getPlayer(turtle, ownable)

    // Sync the turtle's inventory with the fake player
    TurtleFakePlayerProvider.load(fakePlayer, turtle, turtle.direction)

    return try {
      val baseHit = PlayerHelpers.raycast(fakePlayer, 1.5f)
      when (baseHit.type) {
        HitResult.Type.ENTITY -> {
          val hit = baseHit as EntityHitResult
          val result = PlayerInteractionHelpers.attack(fakePlayer, hit.entity)
          FutureMethodResult.result(result.left, result.right)
        }

        HitResult.Type.BLOCK -> {
          val hit = baseHit as BlockHitResult
          val result = fakePlayer.dig(hit.blockPos, hit.side)
          FutureMethodResult.result(result.left, result.right)
        }

        else -> {
          FutureMethodResult.result(false, "Nothing to do here")
        }
      }
    } finally {
      fakePlayer.clearActiveItem()
      TurtleFakePlayerProvider.unload(fakePlayer, turtle)
      fakePlayer.updateCooldown()
    }
  }

  fun getContext(unbaked: IUnbakedContext<IModuleContainer>): TurtleKineticMethodContext {
    val ctx = unbaked.bake()
    val turtle = ContextHelpers.fromSubtarget(ctx, ITurtleAccess::class.java, ORIGIN)
    val ownable = ContextHelpers.fromContext(ctx, IPlayerOwnable::class.java, ORIGIN)
    return TurtleKineticMethodContext(ctx, turtle, ownable)
  }

  data class TurtleKineticMethodContext(
    val context: IContext<IModuleContainer>,
    val turtle: ITurtleAccess,
    val ownable: IPlayerOwnable?
  )
}
