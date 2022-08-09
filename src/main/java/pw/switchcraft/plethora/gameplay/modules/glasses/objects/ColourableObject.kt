package pw.switchcraft.plethora.gameplay.modules.glasses.objects

import net.minecraft.network.PacketByteBuf
import pw.switchcraft.plethora.util.DirtyingProperty

abstract class ColourableObject(id: Int, parent: Int, type: Byte) : BaseObject(id, parent, type), Colourable {
  override var colour by DirtyingProperty(DEFAULT_COLOUR.toInt())

  val red: Int
    get() = colour shr 24 and 0xFF
  val green: Int
    get() = colour shr 16 and 0xFF
  val blue: Int
    get() = colour shr 8 and 0xFF
  val alpha: Int
    get() = colour and 0xFF

  override fun writeInitial(buf: PacketByteBuf) {
    buf.writeInt(colour)
  }

  override fun readInitial(buf: PacketByteBuf) {
    colour = buf.readInt()
  }
}
