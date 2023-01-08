package io.sc3.plethora.gameplay.modules.keyboard

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

data class KeyboardListenPacket(val listening: Boolean) {
  fun toBytes(buf: PacketByteBuf) {
    buf.writeBoolean(listening)
  }

  fun toBytes() =
    PacketByteBufs.create().apply { toBytes(this) }

  companion object {
    fun fromBytes(buf: PacketByteBuf) =
      KeyboardListenPacket(buf.readBoolean())

    @JvmStatic
    fun onReceive(client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf,
                  responseSender: PacketSender) {
      ClientKeyListener.listening = fromBytes(buf).listening
    }
  }
}
