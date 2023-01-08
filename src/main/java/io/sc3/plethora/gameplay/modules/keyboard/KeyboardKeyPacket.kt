package io.sc3.plethora.gameplay.modules.keyboard

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
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
) {
  fun toBytes(buf: PacketByteBuf) {
    buf.writeCollection(presses) { b, p -> p.toBytes(b) }
    buf.writeCollection(chars) { b, c -> c.toBytes(b) }
    buf.writeCollection(releases, PacketByteBuf::writeVarInt)
  }

  fun toBytes() =
    PacketByteBufs.create().apply { toBytes(this) }

  companion object {
    fun fromBytes(buf: PacketByteBuf) = KeyboardKeyPacket(
      buf.readCollection(getMaxValidator({ mutableListOf() }, 128)) { KeyPressEvent.fromBytes(it) },
      buf.readCollection(getMaxValidator({ mutableListOf() }, 128)) { CharEvent.fromBytes(it) },
      buf.readCollection(getMaxValidator({ mutableListOf() }, 128)) { it.readVarInt() }
    )

    @JvmStatic
    fun onReceive(server: MinecraftServer, player: ServerPlayerEntity, handler: ServerPlayNetworkHandler,
                  buf: PacketByteBuf, responseSender: PacketSender) {
      val packet = fromBytes(buf)
      ServerKeyListener.process(player, packet.presses, packet.chars, packet.releases)
    }
  }
}

