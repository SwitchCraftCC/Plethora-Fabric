package io.sc3.plethora.gameplay.modules.keyboard

import io.sc3.library.networking.ScLibraryPacket
import io.sc3.plethora.Plethora.ModId
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.PacketByteBuf.getMaxValidator
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

data class KeyboardKeyPacket(
  val presses:  List<KeyPressEvent>,
  val chars:    List<CharEvent>,
  val releases: List<Int>
): ScLibraryPacket() {
  override val id = KeyboardKeyPacket.id

  override fun toBytes(buf: PacketByteBuf) {
    buf.writeCollection(presses) { b, p -> p.toBytes(b) }
    buf.writeCollection(chars) { b, c -> c.toBytes(b) }
    buf.writeCollection(releases, PacketByteBuf::writeVarInt)
  }

  override fun onServerReceive(server: MinecraftServer, player: ServerPlayerEntity, handler: ServerPlayNetworkHandler,
                               responseSender: PacketSender) {
    ServerKeyListener.process(player, presses, chars, releases)
  }

  companion object {
    @JvmField
    val id = ModId("keyboard_key")

    @JvmStatic
    fun fromBytes(buf: PacketByteBuf) = KeyboardKeyPacket(
      buf.readCollection(getMaxValidator({ mutableListOf() }, 128)) { KeyPressEvent.fromBytes(it) },
      buf.readCollection(getMaxValidator({ mutableListOf() }, 128)) { CharEvent.fromBytes(it) },
      buf.readCollection(getMaxValidator({ mutableListOf() }, 128)) { it.readVarInt() }
    )
  }
}

