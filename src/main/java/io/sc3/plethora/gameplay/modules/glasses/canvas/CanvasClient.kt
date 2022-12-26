package io.sc3.plethora.gameplay.modules.glasses.canvas

import it.unimi.dsi.fastutil.ints.*
import it.unimi.dsi.fastutil.ints.IntIterator
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectGroup

class CanvasClient(val id: Int) {
  private val objects: Int2ObjectMap<BaseObject?> = Int2ObjectOpenHashMap()
  private val childrenOf: Int2ObjectMap<IntSortedSet> = Int2ObjectOpenHashMap()

  init {
    childrenOf.put(CanvasHandler.ID_2D, IntAVLTreeSet())
    childrenOf.put(CanvasHandler.ID_3D, IntAVLTreeSet())
  }

  fun updateObject(obj: BaseObject) {
    val parent: IntSet? = childrenOf[obj.parent]
    if (parent == null) {
      Plethora.log.error("Trying to add ${obj.id} to group ${obj.parent} ($obj)")
      return  // Should never happen but...
    }

    if (objects.put(obj.id, obj) == null) {
      // If this is a new instance then set up the children
      parent.add(obj.id)
      if (obj is ObjectGroup) childrenOf.put(obj.id, IntAVLTreeSet())
    }
  }

  fun remove(id: Int) {
    val obj = objects.remove(id)
    childrenOf.remove(id) // We handle the removing of children in the canvas version

    if (obj != null) {
      // Remove from the parent set if needed.
      val parent: IntSet? = childrenOf[obj.parent]
      parent?.remove(id)
    }
  }

  fun getObject(id: Int) = objects[id]

  fun getChildren(id: Int): IntSet? {
    val children = childrenOf[id]
    return if (children.isEmpty()) null else children
  }

  @Environment(EnvType.CLIENT)
  fun drawChildren(children: IntIterator, matrices: MatrixStack?, consumers: VertexConsumerProvider?) {
    while (children.hasNext()) {
      val id = children.nextInt()
      val obj = getObject(id)
      obj?.draw(this, matrices!!, consumers)
    }
  }
}
