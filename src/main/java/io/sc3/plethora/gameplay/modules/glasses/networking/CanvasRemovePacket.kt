package io.sc3.plethora.gameplay.modules.glasses.networking

import io.sc3.library.networking.ScLibraryPacket
import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler.getClient
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler.removeClient
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

data class CanvasRemovePacket(var canvasId: Int = 0): ScLibraryPacket() {
  override val id = CanvasRemovePacket.id

  override fun toBytes(buf: PacketByteBuf) {
    buf.writeInt(canvasId)
  }

  override fun onClientReceive(client: MinecraftClient, handler: ClientPlayNetworkHandler,
                               responseSender: PacketSender) {
    val canvas = getClient(canvasId) ?: return
    removeClient(canvas)
  }

  companion object {
    @JvmField
    val id = ModId("canvas_remove")

    @JvmStatic
    fun fromBytes(buf: PacketByteBuf) =
      CanvasRemovePacket(buf.readInt())
  }
}
