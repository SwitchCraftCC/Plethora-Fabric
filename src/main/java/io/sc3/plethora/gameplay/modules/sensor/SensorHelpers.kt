package io.sc3.plethora.gameplay.modules.sensor

import io.sc3.plethora.api.IWorldLocation
import io.sc3.plethora.util.EntityHelpers
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import java.util.*
import java.util.function.Predicate

object SensorHelpers {
  fun getBox(pos: BlockPos, radius: Double) = with(pos) {
    Box(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius)
  }

  fun findEntityByUuid(loc: IWorldLocation, radius: Double, uuid: UUID): Entity? =
    loc.world.getEntitiesByClass(Entity::class.java, getBox(loc.pos, radius)) { e ->
      defaultPredicate.test(e) && e.uuid == uuid
    }.firstOrNull()

  fun findEntityByName(loc: IWorldLocation, radius: Double, name: String): Entity? =
    loc.world.getEntitiesByClass(Entity::class.java, getBox(loc.pos, radius)) { e ->
      defaultPredicate.test(e) && (EntityHelpers.getName(e) == name || EntityHelpers.getKey(e) == name)
    }.firstOrNull()

  val defaultPredicate: Predicate<Entity?> = Predicate { e ->
    e != null && e.isAlive && (e !is PlayerEntity || !e.isSpectator())
  }
}
