package pw.switchcraft.plethora.api.method

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaValues
import net.minecraft.util.math.Vec3d

fun IArguments.getVec3dNullable(startIndex: Int = 0): Vec3d? =
  if (count() < startIndex || count() == startIndex && this[0] == null) {
    null
  } else {
    Vec3d(
      getDouble(startIndex),
      getDouble(startIndex + 1),
      getDouble(startIndex + 2)
    )
  }

fun IArguments.getVec3d(startIndex: Int = 0): Vec3d =
  getVec3dNullable(startIndex) ?: throw LuaValues.badArgumentOf(startIndex, "number", get(startIndex))
