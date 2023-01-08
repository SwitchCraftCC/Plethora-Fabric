package io.sc3.plethora.gameplay.modules.keyboard

import io.sc3.plethora.gameplay.registry.Packets.KEYBOARD_KEY_PACKET_ID
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import org.lwjgl.glfw.GLFW.*

object ClientKeyListener {
  var listening = false

  private val keyPressEvents = mutableListOf<KeyPressEvent>()
  private val charEvents = mutableListOf<CharEvent>()
  private val keyReleases = mutableListOf<Int>()

  @JvmStatic
  fun onKeyEvent(key: Int, action: Int) {
    if (!listening || key <= 0) return

    // TODO: This is significantly more primitive than 1.12's keyboard implementation. It no longer keeps track of
    //       key downs and instead relies on GLFW to tell us about key repeats instead of timing them manually. This
    //       should be fine, but behavior may differ from 1.12 in some cases and this will need review.

    when (action) {
      GLFW_PRESS, GLFW_REPEAT -> {
        keyPressEvents.add(KeyPressEvent(key, action == GLFW_REPEAT))
      }
      GLFW_RELEASE -> {
        keyReleases.add(key)
      }
    }
  }

  @JvmStatic
  fun onCharEvent(ch: Int) {
    if (!listening) return

    if (ch in 32..126 || ch in 160..255) {
      charEvents.add(CharEvent(ch.toChar()))
    }
  }

  private fun processEvents() {
    if (!listening) return

    val keyPresses = keyPressEvents.toList()
    val chars = charEvents.toList()
    val releases = keyReleases.toList()

    if (keyPresses.isNotEmpty() || chars.isNotEmpty() || releases.isNotEmpty()) {
      val packet = KeyboardKeyPacket(keyPresses, chars, releases)
      ClientPlayNetworking.send(KEYBOARD_KEY_PACKET_ID, packet.toBytes())

      keyPressEvents.clear()
      charEvents.clear()
      keyReleases.clear()
    }
  }

  fun registerEvents() {
    ClientTickEvents.END_CLIENT_TICK.register { processEvents() }

    ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
      listening = false
      keyPressEvents.clear()
      charEvents.clear()
      keyReleases.clear()
    }
  }
}
