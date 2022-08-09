package pw.switchcraft.plethora.api.method

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaValues.badArgumentOf
import net.minecraft.util.math.Vec3d
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesArgumentHelper
import pw.switchcraft.plethora.util.Vec2d

fun IArguments.getVec2d(startIndex: Int = 0): Vec2d =
  getVec2dNullable(startIndex) ?: throw badArgumentOf(startIndex, "number", get(startIndex))

fun IArguments.getVec2dNullable(startIndex: Int = 0): Vec2d? =
  if (count() < startIndex || count() == startIndex && this[0] == null) {
    null
  } else {
    Vec2d(getDouble(startIndex), getDouble(startIndex + 1))
  }

fun IArguments.getVec2dTable(index: Int = 0): Vec2d =
  GlassesArgumentHelper.getVec2dTable(getTable(index))

fun IArguments.getVec3dNullable(startIndex: Int = 0): Vec3d? =
  if (count() < startIndex || count() == startIndex && this[0] == null) {
    null
  } else {
    Vec3d(getDouble(startIndex), getDouble(startIndex + 1), getDouble(startIndex + 2))
  }

fun IArguments.getVec3d(startIndex: Int = 0): Vec3d =
  getVec3dNullable(startIndex) ?: throw badArgumentOf(startIndex, "number", get(startIndex))

fun IArguments.getVec3dTable(index: Int = 0): Vec3d =
  GlassesArgumentHelper.getVec3dTable(getTable(index))
