package io.sc3.plethora.gameplay.modules.keyboard

import io.sc3.plethora.api.module.IModuleAccess
import io.sc3.plethora.gameplay.registry.Packets.KEYBOARD_LISTEN_PACKET_ID
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object ServerKeyListener {
  private val listeners = WeakHashMap<ServerPlayerEntity, MutableSet<IModuleAccess>>()

  fun add(player: ServerPlayerEntity, access: IModuleAccess) {
    synchronized(listeners) {
      val accesses = listeners.computeIfAbsent(player) { HashSet(1) }

      // Notify the client to start listening
      if (accesses.isEmpty()) {
        ServerPlayNetworking.send(player, KEYBOARD_LISTEN_PACKET_ID, KeyboardListenPacket(true).toBytes())
      }

      accesses.add(access)
    }
  }

  fun remove(player: ServerPlayerEntity, access: IModuleAccess) {
    synchronized(listeners) {
      val accesses = listeners[player] ?: return

      // Notify the client to stop listening
      if (accesses.remove(access) && accesses.isEmpty()) {
        ServerPlayNetworking.send(player, KEYBOARD_LISTEN_PACKET_ID, KeyboardListenPacket(false).toBytes())
      }
    }
  }

  fun clear() {
    synchronized(listeners) { listeners.clear() }
  }

  fun process(player: ServerPlayerEntity, presses: List<KeyPressEvent>, chars: List<CharEvent>, releases: List<Int>) {
    synchronized(listeners) {
      val accesses = listeners[player] ?: return
      accesses.forEach { access ->
        presses.forEach {
          if (it.key > 0) access.queueEvent("key", it.key, it.repeat)
        }

        chars.forEach {
          if (it.char.code in 32..126 || it.char.code in 160..255) {
            access.queueEvent("char", it.char.toString())
          }
        }

        releases.forEach {
          access.queueEvent("key_up", it)
        }
      }
    }
  }

  @JvmStatic
  fun registerEvents() {
    ServerLifecycleEvents.SERVER_STARTING.register { clear() }
    ServerLifecycleEvents.SERVER_STOPPING.register { clear() }
  }
}
