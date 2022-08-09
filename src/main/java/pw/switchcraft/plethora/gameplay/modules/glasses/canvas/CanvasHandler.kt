package pw.switchcraft.plethora.gameplay.modules.glasses.canvas

import com.mojang.blaze3d.systems.RenderSystem
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.util.NbtType.COMPOUND
import net.fabricmc.fabric.api.util.NbtType.NUMBER
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler.MODULE_DATA
import pw.switchcraft.plethora.gameplay.neural.NeuralHelpers
import pw.switchcraft.plethora.gameplay.registry.Packets
import pw.switchcraft.plethora.gameplay.registry.Packets.CANVAS_ADD_PACKET_ID
import pw.switchcraft.plethora.gameplay.registry.Packets.CANVAS_REMOVE_PACKET_ID
import pw.switchcraft.plethora.gameplay.registry.PlethoraModules.GLASSES_S
import java.util.concurrent.atomic.AtomicInteger

object CanvasHandler {
  const val ID_2D = 0
  const val ID_3D = 1

  const val WIDTH = 512
  const val HEIGHT = 512 / 16 * 9

  private val id = AtomicInteger(0)
  private val server = HashSet<CanvasServer>()

  private val client: Int2ObjectMap<CanvasClient> = Int2ObjectOpenHashMap()

  fun nextId() = id.getAndIncrement()

  fun addServer(canvas: CanvasServer) {
    synchronized(server) {
      server.add(canvas)
      ServerPlayNetworking.send(canvas.player, CANVAS_ADD_PACKET_ID, canvas.makeAddPacket().toBytes())
    }
  }

  fun removeServer(canvas: CanvasServer) {
    synchronized(server) {
      server.remove(canvas)
      ServerPlayNetworking.send(canvas.player, CANVAS_REMOVE_PACKET_ID, canvas.makeRemovePacket().toBytes())
    }
  }

  @JvmStatic
  fun addClient(canvas: CanvasClient) {
    synchronized(client) { client.put(canvas.id, canvas) }
  }

  @JvmStatic
  fun removeClient(canvas: CanvasClient) {
    synchronized(client) { client.remove(canvas.id) }
  }

  @JvmStatic
  fun getClient(id: Int): CanvasClient {
    synchronized(client) { return client[id] }
  }

  fun clear() {
    synchronized(server) { server.clear() }
    synchronized(client) { client.clear() }
  }

  fun update() {
    synchronized(server) {
      for (canvas in server) {
        val packet = canvas.makeUpdatePacket()
        if (packet != null) {
          ServerPlayNetworking.send(canvas.player, Packets.CANVAS_UPDATE_PACKET_ID, packet.toBytes())
        }
      }
    }
  }

  @Environment(EnvType.CLIENT)
  fun getCanvas(client: MinecraftClient): CanvasClient? {
    val player: PlayerEntity? = client.player

    val optStack = NeuralHelpers.getStack(player)
    if (optStack.isEmpty) return null
    val stack = optStack.get()

    val nbt = stack.nbt
    if (nbt == null || !nbt.contains(MODULE_DATA, COMPOUND)) return null

    val modules = nbt.getCompound(MODULE_DATA)
    if (!modules.contains(GLASSES_S, COMPOUND)) return null

    val data = modules.getCompound(GLASSES_S)
    if (!data.contains("id", NUMBER)) return null

    val id = data.getInt("id")
    return getClient(id)
  }

  @JvmStatic
  fun render2DOverlay(client: MinecraftClient, matrices: MatrixStack) {
    val canvas = getCanvas(client) ?: return

    // If we've no text renderer then we're probably not quite ready yet
    if (client.textRenderer == null) return

    matrices.push()

    // The hotbar renders at -90 (see InGameGui#renderHotbar)
    matrices.translate(0.0, 0.0, -100.0)
    matrices.scale(client.window.scaledWidth.toFloat() / WIDTH, client.window.scaledHeight.toFloat() / HEIGHT, 2f)

    synchronized(canvas) {
      canvas.drawChildren(canvas.getChildren(ID_2D).iterator(), matrices, null)
    }

    RenderSystem.enableTexture()
    RenderSystem.enableCull()

    matrices.pop()
  }

  private fun onWorldRender(ctx: WorldRenderContext) {
    val canvas = getCanvas(MinecraftClient.getInstance()) ?: return
    synchronized(canvas) {
      canvas.drawChildren(canvas.getChildren(ID_3D).iterator(), ctx.matrixStack(), ctx.consumers())
    }

    // TODO: GL state
  }

  @JvmStatic
  fun registerServerEvents() {
    ServerTickEvents.START_SERVER_TICK.register { update() }
  }

  @JvmStatic
  @Environment(EnvType.CLIENT)
  fun registerClientEvents() {
    WorldRenderEvents.AFTER_TRANSLUCENT.register(::onWorldRender)
  }
}
