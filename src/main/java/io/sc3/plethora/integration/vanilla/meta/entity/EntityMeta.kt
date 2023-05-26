package io.sc3.plethora.integration.vanilla.meta.entity

import io.sc3.plethora.api.IWorldLocation
import io.sc3.plethora.api.meta.BaseMetaProvider
import io.sc3.plethora.api.method.ContextKeys.ORIGIN
import io.sc3.plethora.api.method.IPartialContext
import io.sc3.plethora.util.EntityHelpers
import io.sc3.plethora.util.Helpers
import io.sc3.plethora.util.VelocityDeterminable
import net.minecraft.entity.Entity
import net.minecraft.util.math.Direction
import java.util.*

object EntityMeta : BaseMetaProvider<Entity>(
  description = "Provides some basic information about an entity, such as their their UUID and name."
) {
  private val allAxes = EnumSet.allOf(Direction.Axis::class.java)

  override fun getMeta(ctx: IPartialContext<Entity>): Map<String, *> {
    val entity = ctx.target
    val location = ctx.getContext(ORIGIN, IWorldLocation::class.java)

    val result = getBasicProperties(entity, location)

    val pos = entity.eyePos.subtract(entity.eyePos.floorAlongAxes(allAxes))
    result["withinBlock"] = mapOf(
      "x" to pos.x,
      "y" to pos.y,
      "z" to pos.z
    )

    return result
  }

  @JvmStatic
  fun getBasicProperties(entity: Entity, location: IWorldLocation?): MutableMap<String, Any> {
    val motion = entity.velocity
    val deltaPos = (entity as VelocityDeterminable).deltaPos

    val result = mutableMapOf<String, Any>(
      "id" to entity.uuid.toString(),

      // TODO: Is this a good idea to add? In 1.12, block IDs are returned but not entity IDs, so you have to do some
      //       guesswork based on the names of the entity.
      "key"         to EntityHelpers.getKey(entity),
      "name"        to EntityHelpers.getName(entity),
      "displayName" to entity.name.string,

      // Server-side velocity. Only includes velocity that was initiated from the server, when the player is not on
      // the ground. This will update immediately, but will not reflect all changes of the player's position.
      "motionX" to motion.x,
      "motionY" to motion.y,
      "motionZ" to motion.z,

      // Client-side velocity. Includes velocity that was initiated from the client, such as when the player is
      // moving around. This will NOT update immediately, and some changes may be delayed according to the player's
      // network latency, as the client is responsible for updating the player's position.
      "deltaPosX" to deltaPos.x,
      "deltaPosY" to deltaPos.y,
      "deltaPosZ" to deltaPos.z,

      "pitch" to Helpers.normaliseAngle(entity.pitch.toDouble()),
      "yaw"   to Helpers.normaliseAngle(entity.yaw.toDouble())
    )

    if (location != null && location.world === entity.entityWorld) {
      val pos = entity.eyePos.subtract(location.loc)
      result["x"] = pos.x
      result["y"] = pos.y
      result["z"] = pos.z
    }

    return result
  }
}
