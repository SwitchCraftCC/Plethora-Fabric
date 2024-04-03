package io.sc3.plethora.api.method

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaValues
import dan200.computercraft.api.lua.LuaValues.badArgumentOf
import io.sc3.plethora.util.Vec2d
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

object ArgumentExt {
  fun IArguments.getVec2d(startIndex: Int = 0): Vec2d =
    getVec2dNullable(startIndex) ?: throw badArgumentOf(this, startIndex, "number")

  fun IArguments.getVec2dNullable(startIndex: Int = 0): Vec2d? =
    if (count() < startIndex || count() == startIndex && this[0] == null) {
      null
    } else {
      Vec2d(getDouble(startIndex), getDouble(startIndex + 1))
    }

  fun IArguments.getVec2dTable(index: Int = 0): Vec2d {
    val point = getTable(index)
    return if (point.containsKey("x")) {
      Vec2d(point.getFiniteDouble("x"), point.getFiniteDouble("y"))
    } else {
      Vec2d(point.getFiniteDouble(1.0), point.getFiniteDouble(2.0))
    }
  }

  fun IArguments.getVec3dNullable(startIndex: Int = 0): Vec3d? =
    if (count() < startIndex || count() == startIndex && this[0] == null) {
      null
    } else {
      Vec3d(getDouble(startIndex), getDouble(startIndex + 1), getDouble(startIndex + 2))
    }

  fun IArguments.getVec3d(startIndex: Int = 0): Vec3d =
    getVec3dNullable(startIndex) ?: throw badArgumentOf(this, startIndex, "number")

  fun IArguments.getVec3dTable(index: Int = 0): Vec3d {
    val point = getTable(index)
    return if (point.containsKey("x")) {
      Vec3d(point.getFiniteDouble("x"), point.getFiniteDouble("y"), point.getFiniteDouble("z"))
    } else {
      Vec3d(point.getFiniteDouble(1.0), point.getFiniteDouble(2.0), point.getFiniteDouble(3.0))
    }
  }

  @JvmStatic
  fun assertIntBetween(value: Int, min: Int, max: Int, message: String): Int {
    if (value < min || value > max) {
      throw LuaException(String.format(message, "between $min and $max"))
    }
    return value
  }

  @JvmStatic
  fun IArguments.assertIntBetween(index: Int, min: Int, max: Int, message: String) =
    ArgumentExt.assertIntBetween(getInt(index), min, max, message)

  @JvmStatic
  fun assertDoubleBetween(value: Double, min: Double, max: Double, message: String): Double {
    if (value < min || value > max || value.isNaN()) {
      throw LuaException(String.format(message, "between $min and $max"))
    }
    return value
  }

  @JvmStatic
  fun IArguments.assertDoubleBetween(index: Int, min: Double, max: Double, message: String) =
    assertDoubleBetween(getFiniteDouble(index), min, max, message)

  @JvmStatic
  fun IArguments.assertStringLength(index: Int, min: Int, max: Int,
                                    message: String = "string length out of bounds (%s)"): String {
    val value = getString(index)
    ArgumentExt.assertIntBetween(value.length, min, max, message)
    return value
  }

  fun IArguments.optHand(index: Int): Hand {
    return when (val hand = optString(index, "main")!!.lowercase()) {
      "main", "mainhand" -> Hand.MAIN_HAND
      "off", "offhand" -> Hand.OFF_HAND
      else -> throw LuaException("Unknown hand '$hand', expected 'main' or 'off'")
    }
  }

  fun IArguments.getItem(index: Int): Item {
    val id = Identifier(getString(index))
    if (!Registries.ITEM.containsId(id)) throw LuaException("Unknown item '$id'")
    return Registries.ITEM[id]
  }

  private fun Map<*, *>.getFiniteDouble(key: Any): Double {
    val obj = this[key] ?: throw LuaException("Expected number for key $key, got nil")
    if (obj !is Number) throw badKey(obj, key.toString(), "number")

    val value = obj.toDouble()
    if (!value.isFinite()) throw badKey(obj, key.toString(), "number")

    return value
  }

  private fun badKey(obj: Any?, key: String, expected: String) =
    LuaException("Expected " + expected + " for key " + key + ", got " + LuaValues.getType(obj))
}
