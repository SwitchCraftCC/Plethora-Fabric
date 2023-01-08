package io.sc3.plethora.gameplay.modules.keyboard

import net.minecraft.network.PacketByteBuf

data class KeyPressEvent(
  val key: Int,
  val repeat: Boolean
) {
  fun toBytes(buf: PacketByteBuf) {
    buf.writeVarInt(key)
    buf.writeBoolean(repeat)
  }

  companion object {
    fun fromBytes(buf: PacketByteBuf) = KeyPressEvent(
      key = buf.readVarInt(),
      repeat = buf.readBoolean()
    )
  }
}

data class CharEvent(val char: Char) {
  fun toBytes(buf: PacketByteBuf) {
    buf.writeChar(char.code)
  }

  companion object {
    fun fromBytes(buf: PacketByteBuf) =
      CharEvent(char = buf.readChar())
  }
}
