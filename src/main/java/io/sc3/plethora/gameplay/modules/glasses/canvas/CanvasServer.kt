package pw.switchcraft.plethora.gameplay.modules.glasses.canvas

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.server.network.ServerPlayerEntity
import pw.switchcraft.plethora.api.method.IAttachable
import pw.switchcraft.plethora.api.module.IModuleAccess
import pw.switchcraft.plethora.api.reference.ConstantReference
import pw.switchcraft.plethora.api.reference.IReference
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.ID_2D
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.ID_3D
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasAddPacket
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasRemovePacket
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasUpdatePacket
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject.BaseObjectReference
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Frame2d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Origin3d
import java.util.concurrent.atomic.AtomicInteger

class CanvasServer(
  access: IModuleAccess,
  player: ServerPlayerEntity
) : ConstantReference<CanvasServer>, IAttachable {
  private val canvasId: Int = CanvasHandler.nextId()
  private val access: IModuleAccess
  val player: ServerPlayerEntity

  private val objects: Int2ObjectMap<BaseObject?> = Int2ObjectOpenHashMap()
  private val childrenOf: Int2ObjectMap<IntSet> = Int2ObjectOpenHashMap()

  private val removed: IntSet = IntOpenHashSet()

  private val lastId = AtomicInteger(ID_3D)

  val canvas2d = object: Frame2d { override val id = ID_2D }
  val canvas3d = object: Origin3d { override val id = ID_3D }

  init {
    this.access = access
    this.player = player
    childrenOf.put(ID_2D, IntOpenHashSet())
    childrenOf.put(ID_3D, IntOpenHashSet())
  }

  override fun attach() {
    access.data.putInt("id", canvasId)
    access.markDataDirty()
    CanvasHandler.addServer(this)
  }

  override fun detach() {
    CanvasHandler.removeServer(this)
    access.data.remove("id")
    access.markDataDirty()
  }

  fun newObjectId() = lastId.incrementAndGet()

  @Synchronized
  fun makeAddPacket() = CanvasAddPacket(canvasId, objects.values.toTypedArray())

  @Synchronized
  fun makeRemovePacket() = CanvasRemovePacket(canvasId)

  @Synchronized
  fun makeUpdatePacket(): CanvasUpdatePacket? {
    var changed: MutableList<BaseObject>? = null
    for (obj in objects.values) {
      if (obj!!.pollDirty()) {
        if (changed == null) changed = mutableListOf()
        changed.add(obj)
      }
    }

    if (changed == null && removed.isEmpty()) return null
    if (changed == null) changed = mutableListOf()

    val packet = CanvasUpdatePacket(canvasId, changed, removed.toIntArray())
    removed.clear()
    return packet
  }

  @Synchronized
  fun add(obj: BaseObject) {
    val parent = childrenOf[obj.parent] ?: throw IllegalArgumentException("No such parent")
    check(objects.put(obj.id, obj) == null) { "An object already exists with that key" }
    parent.add(obj.id)
    if (obj is ObjectGroup) childrenOf.put(obj.id, IntOpenHashSet())
  }

  @Synchronized
  fun remove(obj: BaseObject) {
    check(removeImpl(obj.id)) { "No such object with this key" }
  }

  @Suppress("UNCHECKED_CAST")
  @Synchronized
  fun <T : BaseObject?> getObject(id: Int): T? = objects[id] as T?

  @Synchronized
  fun clear(obj: ObjectGroup) {
    val children = childrenOf[obj.id] ?: throw IllegalStateException("Object has no children")
    clearImpl(children)
  }

  private fun removeImpl(id: Int): Boolean {
    if (objects.remove(id) == null) return false

    val children = childrenOf.remove(id)
    children?.let { clearImpl(it) }

    removed.add(id)
    return true
  }

  private fun clearImpl(objects: IntSet) {
    val iterator = objects.iterator()
    while (iterator.hasNext()) {
      val childId = iterator.nextInt()
      removeImpl(childId)
      iterator.remove()
    }
  }

  override fun get(): CanvasServer = this
  override fun safeGet(): CanvasServer = this

  /**
   * Get a reference to this object
   *
   * @param baseObject@return The resulting reference.
   */
  fun <T : BaseObject> reference(baseObject: T): IReference<T> = BaseObjectReference(this, baseObject)
}
