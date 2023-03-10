package io.sc3.plethora.gameplay.modules.glasses.objects

import com.mojang.blaze3d.systems.RenderSystem
import dan200.computercraft.api.lua.LuaException
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.PacketByteBuf
import io.sc3.plethora.api.reference.ConstantReference
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasServer
import io.sc3.plethora.util.Dirtyable

abstract class BaseObject(
  val id: Int,
  val parent: Int,
  val type: Byte
) : Dirtyable {
  private var dirty = true

  override fun pollDirty(): Boolean {
    val value = dirty
    dirty = false
    return value
  }

  override fun setDirty() {
    dirty = true
  }

  /**
   * Read the initial data for this object.
   *
   * @param buf The buffer to read from.
   */
  abstract fun readInitial(buf: PacketByteBuf)

  /**
   * Write the initial buffer for this object.
   *
   * @param buf The buffer to write to.
   */
  abstract fun writeInitial(buf: PacketByteBuf)

  /**
   * Draw this object
   *
   * @param canvas    The canvas context we are drawing within
   * @param matrices
   * @param consumers
   */
  @Environment(EnvType.CLIENT)
  abstract fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?)

  class BaseObjectReference<T : BaseObject>(
    private val canvas: CanvasServer,
    obj: BaseObject
  ) : ConstantReference<T> {
    private val id: Int
    init {
      id = obj.id
    }

    override fun get(): T =
      canvas.getObject(id) ?: throw LuaException("This object has been removed")
    override fun safeGet(): T = get()
  }

  fun interface Factory {
    fun create(id: Int, parent: Int): BaseObject
  }

  companion object {
    @JvmField
    val SORTING_ORDER: java.util.Comparator<BaseObject> =
      Comparator.comparingInt { a: BaseObject -> a.id }

    /**
     * Prepare to draw a flat object
     */
    @JvmStatic
    @Environment(EnvType.CLIENT)
    protected fun setupFlat() {
      RenderSystem.disableCull()
      RenderSystem.enableBlend()
      RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
    }
  }
}
