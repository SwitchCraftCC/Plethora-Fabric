package pw.switchcraft.plethora.gameplay.modules.laser

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.turtle.ITurtleAccess
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.api.IPlayerOwnable
import pw.switchcraft.plethora.api.IWorldLocation
import pw.switchcraft.plethora.api.method.ArgumentHelper.assertBetween
import pw.switchcraft.plethora.api.method.ContextKeys
import pw.switchcraft.plethora.api.method.FutureMethodResult
import pw.switchcraft.plethora.api.method.IUnbakedContext
import pw.switchcraft.plethora.api.module.IModuleContainer
import pw.switchcraft.plethora.api.module.SubtargetedModuleMethod
import pw.switchcraft.plethora.gameplay.registry.PlethoraModules
import pw.switchcraft.plethora.util.Helpers
import pw.switchcraft.plethora.util.PlayerHelpers
import java.lang.Math.PI
import java.util.concurrent.Callable
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LaserMethods {
  private val cfg by Plethora.config::laser

  val FIRE = SubtargetedModuleMethod.of(
    "fire", PlethoraModules.LASER_M, IWorldLocation::class.java,
    "function(yaw:number, pitch:number, potency:number) -- Fire a laser in a set direction",
    ::fire
  )
  private fun fire(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val yaw = Helpers.normaliseAngle(args.getFiniteDouble(0))
    val pitch = Helpers.normaliseAngle(args.getFiniteDouble(1))
    val potency = assertBetween(args.getFiniteDouble(2), cfg.minimumPotency, cfg.maximumPotency, "Potency out of range (%s).").toFloat()

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
        val vOff = 0.3 // The laser is 0.25 high, so we add a little more.

        // Offset positions to be around the edge of the manipulator. Avoids breaking the manipulator and the
        // block below/above in most cases.
        // Also offset to be just above/below the manipulator, depending on the pitch.

        val offset = if (pitch < -60) {
          Vec3d(0.0, 0.5 + vOff, 0.0)
        } else if (pitch > 60) {
          Vec3d(0.0, -0.5 - vOff, 0.0)
        } else {
          // The laser is 0.25 wide, the offset from the centre is 0.5.
          val hOff = 0.9
          val length = sqrt(motionX * motionX + motionZ * motionZ)
          Vec3d(motionX / length * hOff, 0.0, motionZ / length * hOff)
        }

        laser.setPosition(pos.add(offset))
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

      laser.setPotency(potency)
      laser.shoot(motionX, motionY, motionZ, 1.5f, 0.0f)

      location.world.spawnEntity(laser)

      FutureMethodResult.empty()
    }))
  }
}
