package io.sc3.plethora.gameplay.modules.laser

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.turtle.ITurtleAccess
import io.sc3.plethora.Plethora
import io.sc3.plethora.api.IPlayerOwnable
import io.sc3.plethora.api.IWorldLocation
import io.sc3.plethora.api.method.ArgumentExt.assertDoubleBetween
import io.sc3.plethora.api.method.ContextKeys
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.SubtargetedModuleMethod
import io.sc3.plethora.gameplay.registry.PlethoraModules
import io.sc3.plethora.util.Helpers
import io.sc3.plethora.util.PlayerHelpers
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import java.lang.Math.PI
import java.util.concurrent.Callable
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LaserMethods {
  private val cfg
    get() = Plethora.config.laser

  val FIRE = SubtargetedModuleMethod.of(
    "fire", PlethoraModules.LASER_M, IWorldLocation::class.java,
    "function(yaw:number, pitch:number, potency:number) -- Fire a laser in a set direction",
    ::fire
  )
  private fun fire(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val yaw = Helpers.normaliseAngle(args.getFiniteDouble(0))
    val pitch = Helpers.normaliseAngle(args.getFiniteDouble(1))
    val potency = args.assertDoubleBetween(2, cfg.minimumPotency, cfg.maximumPotency, "Potency out of range (%s).").toFloat()

    val motionX = -sin(yaw / 180.0f * PI.toFloat()) * cos(pitch / 180.0f * PI.toFloat())
    val motionZ =  cos(yaw / 180.0f * PI.toFloat()) * cos(pitch / 180.0f * PI.toFloat())
    val motionY = -sin(pitch / 180.0f * PI.toFloat())

    return unbaked.costHandler.await(potency * cfg.cost, FutureMethodResult.nextTick(Callable {
      val ctx = unbaked.bake()
      val location = ctx.getContext(ContextKeys.ORIGIN, IWorldLocation::class.java)
      val pos = location.loc

      val laser = LaserEntity(location.world, pos)

      val ownable = ctx.getContext(ContextKeys.ORIGIN, IPlayerOwnable::class.java)
      val entity = ctx.getContext(ContextKeys.ORIGIN, Entity::class.java)

      val profile = ownable?.owningProfile ?: PlayerHelpers.getProfile(entity)
      laser.setShooter(entity, profile)

      if (ctx.hasContext(BlockEntity::class.java) || ctx.hasContext(ITurtleAccess::class.java)) {
        laser.setPosition(pos.add(Vec3d(0.5, 0.5, 0.5)))
        laser.setPosition(pos.add(Vec3d(
          MathHelper.clamp(motionX * 1.25, -0.75, 0.75),
          MathHelper.clamp(motionY * 1.25, -0.75, 0.75),
          MathHelper.clamp(motionZ * 1.25, -0.75, 0.75)
        )))
      } else if (ctx.hasContext(Entity::class.java)) {
        val entity = ctx.getContext(Entity::class.java)
        val vector = entity.pos
        val offset = entity.width + 0.2
        val length = sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ)

        // Offset positions to be around the edge of the entity. Avoids damaging the entity.
        laser.setPosition(vector.add(
          motionX / length * offset,
          entity.standingEyeHeight + motionY / length * offset,
          motionZ / length * offset
        ))
      } else {
        laser.setPosition(pos)
      }

      laser.potency = potency
      laser.shoot(motionX, motionY, motionZ, 1.5f, 0.0f)

      location.world.spawnEntity(laser)

      FutureMethodResult.empty()
    }))
  }
}
