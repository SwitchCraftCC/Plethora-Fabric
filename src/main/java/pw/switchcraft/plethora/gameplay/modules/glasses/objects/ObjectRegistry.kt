package pw.switchcraft.plethora.gameplay.modules.glasses.objects

import net.minecraft.network.PacketByteBuf
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject.Factory
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.*
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d.Box3d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d.Item3d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d.ObjectFrame3d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d.ObjectRoot3d

object ObjectRegistry {
  // 2D
  const val RECTANGLE_2D: Byte = 0
  const val LINE_2D: Byte = 1
  const val DOT_2D: Byte = 2
  const val TEXT_2D: Byte = 3
  const val TRIANGLE_2D: Byte = 4
  const val POLYGON_2D: Byte = 5
  const val LINE_LOOP_2D: Byte = 6
  const val ITEM_2D: Byte = 7
  const val GROUP_2D: Byte = 8

  // 3D
  const val ORIGIN_3D: Byte = 9
  const val FRAME_3D: Byte = 10
  const val BOX_3D: Byte = 11
  const val ITEM_3D: Byte = 12
  // const val LINE_3D: Byte = 13

  private val FACTORIES = arrayOf(
    // 2D
    Factory { id, parent -> Rectangle2d(id, parent) },
    Factory { id, parent -> Line2d(id, parent) },
    Factory { id, parent -> Dot2d(id, parent) },
    Factory { id, parent -> Text2d(id, parent) },
    Factory { id, parent -> Triangle2d(id, parent) },
    Factory { id, parent -> Polygon2d(id, parent) },
    Factory { id, parent -> LineLoop2d(id, parent) },
    Factory { id, parent -> Item2d(id, parent) },
    Factory { id, parent -> ObjectGroup2d(id, parent) },

    // 3D
    Factory { id, parent -> ObjectRoot3d(id, parent) },
    Factory { id, parent -> ObjectFrame3d(id, parent) },
    Factory { id, parent -> Box3d(id, parent) },
    Factory { id, parent -> Item3d(id, parent) },
    null, // Line3d
  )

  fun create(id: Int, parent: Int, type: Byte): BaseObject {
    check(!(type < 0 || type >= FACTORIES.size)) { "Unknown type $type" }
    val factory = FACTORIES[type.toInt()] ?: throw IllegalStateException("No factory for type $type")

    val obj = factory.create(id, parent)
    check(obj.type == type) { "Created object of type " + obj.type + ", expected " + type }

    return obj
  }

  @JvmStatic
  fun read(buf: PacketByteBuf): BaseObject {
    val id = buf.readVarInt()
    val parent = buf.readVarInt()
    val type = buf.readByte()

    val obj = create(id, parent, type)
    obj.readInitial(buf)
    return obj
  }

  @JvmStatic
  fun write(buf: PacketByteBuf, obj: BaseObject) {
    buf.writeVarInt(obj.id)
    buf.writeVarInt(obj.parent)
    buf.writeByte(obj.type.toInt())
    obj.writeInitial(buf)
  }
}
