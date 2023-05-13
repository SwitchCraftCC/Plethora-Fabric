package io.sc3.plethora.gameplay.modules.glasses.networking

import io.sc3.library.networking.ScLibraryPacket
import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler.getClient
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.read
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.write
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

data class CanvasUpdatePacket(
  var canvasId: Int = 0,
  var changed: MutableList<BaseObject> = mutableListOf(),
  var removed: IntArray = IntArray(0)
): ScLibraryPacket() {
  override val id = CanvasUpdatePacket.id

  override fun toBytes(buf: PacketByteBuf) {
    buf.writeInt(canvasId)
    buf.writeCollection(changed, ::write)
    buf.writeIntArray(removed)
  }

  companion object {
    @JvmField
    val id = ModId("canvas_update")

    @JvmStatic
    fun fromBytes(buf: PacketByteBuf): CanvasUpdatePacket {
      val canvasId = buf.readInt()
      val changed = buf.readCollection({ mutableListOf<BaseObject>() }, ::read)
      val removed = buf.readIntArray()

      changed.sortWith(BaseObject.SORTING_ORDER) // Sort by ID to guarantee parents are loaded before their children

      return CanvasUpdatePacket(canvasId, changed, removed)
    }
  }

  override fun onClientReceive(client: MinecraftClient, handler: ClientPlayNetworkHandler,
                               responseSender: PacketSender) {
    val canvas = getClient(canvasId) ?: return
    synchronized(canvas) {
      changed.onEach(canvas::updateObject)
      removed.onEach(canvas::remove)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CanvasUpdatePacket

    if (canvasId != other.canvasId) return false
    if (changed != other.changed) return false
    if (!removed.contentEquals(other.removed)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = canvasId
    result = 31 * result + changed.hashCode()
    result = 31 * result + removed.contentHashCode()
    return result
  }
}
