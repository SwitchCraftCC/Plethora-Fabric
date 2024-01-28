package io.sc3.plethora.util

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import java.util.*

object EntityHelpers {
  @JvmStatic
  fun getEntityFromUuid(server: MinecraftServer, uuid: UUID): Entity? {
    for (world in server.worlds) {
      world?.getEntity(uuid)?.let { return it }
    }

    return null
  }

  // TODO: Verify this matches the original logic
  fun getName(e: Entity): String = when (e) {
    is PlayerEntity -> e.getName().string
    else -> e.type.name.string
  }

  fun getKey(e: Entity): String =
    Registries.ENTITY_TYPE.getId(e.type).toString()
}
