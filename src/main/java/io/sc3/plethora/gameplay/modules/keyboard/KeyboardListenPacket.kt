package io.sc3.plethora.gameplay.modules.keyboard

import io.sc3.library.networking.ScLibraryPacket
import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.gameplay.modules.glasses.networking.CanvasAddPacket
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

data class KeyboardListenPacket(val listening: Boolean): ScLibraryPacket() {
  override val id = CanvasAddPacket.id

  override fun toBytes(buf: PacketByteBuf) {
    buf.writeBoolean(listening)
  }

  override fun onClientReceive(client: MinecraftClient, handler: ClientPlayNetworkHandler,
                               responseSender: PacketSender) {
    ClientKeyListener.listening = listening
  }

  companion object {
    @JvmField
    val id = ModId("keyboard_listen")

    @JvmStatic
    fun fromBytes(buf: PacketByteBuf) =
      KeyboardListenPacket(buf.readBoolean())
  }
}
