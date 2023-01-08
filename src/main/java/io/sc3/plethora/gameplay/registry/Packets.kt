package io.sc3.plethora.gameplay.registry

import io.sc3.plethora.Plethora.ModId

object Packets {
  @JvmField val SPAWN_PACKET_ID           = ModId("spawn_packet")
  @JvmField val CANVAS_ADD_PACKET_ID      = ModId("canvas_add_packet")
  @JvmField val CANVAS_REMOVE_PACKET_ID   = ModId("canvas_remove_packet")
  @JvmField val CANVAS_UPDATE_PACKET_ID   = ModId("canvas_update_packet")
  @JvmField val KEYBOARD_LISTEN_PACKET_ID = ModId("keyboard_listen_packet")
  @JvmField val KEYBOARD_KEY_PACKET_ID    = ModId("keyboard_key_packet")
}
