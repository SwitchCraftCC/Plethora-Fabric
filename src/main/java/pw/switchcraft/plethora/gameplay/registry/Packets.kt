package pw.switchcraft.plethora.gameplay.registry

import pw.switchcraft.plethora.Plethora.ModId

object Packets {
  @JvmField val SPAWN_PACKET_ID = ModId("spawn_packet")
  @JvmField val CANVAS_ADD_PACKET_ID = ModId("canvas_add_packet")
  @JvmField val CANVAS_REMOVE_PACKET_ID = ModId("canvas_remove_packet")
  @JvmField val CANVAS_UPDATE_PACKET_ID = ModId("canvas_update_packet")
}
