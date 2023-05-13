package io.sc3.plethora.gameplay.modules.glasses.networking

import io.sc3.library.networking.ScLibraryPacket
import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.read
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.write
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

data class CanvasAddPacket(
  var canvasId: Int = 0,
  var objects: Collection<BaseObject> = emptyList()
): ScLibraryPacket() {
  override val id = CanvasAddPacket.id

  override fun toBytes(buf: PacketByteBuf) {
    buf.writeInt(canvasId)
    buf.writeCollection(objects, ::write)
  }

  override fun onClientReceive(client: MinecraftClient, handler: ClientPlayNetworkHandler,
                               responseSender: PacketSender) {
    val canvas = CanvasClient(canvasId)
    objects.onEach(canvas::updateObject)
    CanvasHandler.addClient(canvas)
  }

  companion object {
    @JvmField
    val id = ModId("canvas_add")

    @JvmStatic
    fun fromBytes(buf: PacketByteBuf): CanvasAddPacket {
      val canvasId = buf.readInt()
      val objects = buf.readCollection({ mutableListOf<BaseObject>() }, ::read)

      objects.sortWith(BaseObject.SORTING_ORDER) // Sort by ID to guarantee parents are loaded before their children

      return CanvasAddPacket(canvasId, objects)
    }
  }
}
