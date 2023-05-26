package io.sc3.plethora.integration

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.lua.LuaException
import io.sc3.plethora.api.IPlayerOwnable
import io.sc3.plethora.api.reference.ConstantReference
import io.sc3.plethora.util.EntityHelpers
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.MinecraftServer
import java.util.*

open class EntityIdentifier private constructor(
  val id: UUID,
  val name: String?
) : ConstantReference<EntityIdentifier> {
  constructor(entity: Entity) : this(entity.uuid, null)

  override fun get() = this
  override fun safeGet() = this

  fun getEntity(server: MinecraftServer): LivingEntity =
    EntityHelpers.getEntityFromUuid(server, id) as? LivingEntity
      ?: throw LuaException("Cannot find entity")

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val that = other as EntityIdentifier
    return id == that.id && name == that.name
  }

  override fun hashCode() = Objects.hash(id, name)

  class Player(private val profile: GameProfile) : EntityIdentifier(profile.id, profile.name), IPlayerOwnable {
    override fun getOwningProfile() = profile

    fun getPlayer(server: MinecraftServer) =
      server.playerManager.getPlayer(profile.id)
        ?: throw LuaException("Player is not online")
  }
}
