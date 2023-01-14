package io.sc3.plethora.gameplay.redstone

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.server.world.ServerWorld
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object RedstoneIntegratorTicker {
  private val toTick = Collections.newSetFromMap(ConcurrentHashMap<RedstoneIntegratorBlockEntity, Boolean>())

  fun enqueueTick(be: RedstoneIntegratorBlockEntity) {
    toTick.add(be)
  }

  fun handleTick() {
    val it = toTick.iterator()
    while (it.hasNext()) {
      val be = it.next()
      be.updateOnce()
      it.remove()
    }
  }

  fun handleUnload(eventWorld: ServerWorld) {
    if (eventWorld.isClient) return
    val it = toTick.iterator()
    while (it.hasNext()) {
      val world = it.next().world
      if (world == null || world === eventWorld) it.remove()
    }
  }

  @JvmStatic
  fun registerEvents() {
    ServerTickEvents.START_SERVER_TICK.register { handleTick() }
    ServerWorldEvents.UNLOAD.register { _, world -> handleUnload(world) }
  }
}
