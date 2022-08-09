package pw.switchcraft.plethora.api.method

import net.minecraft.util.math.Vec3d
import pw.switchcraft.plethora.util.Vec2d

fun Vec2d?.toResult(): FutureMethodResult =
  this?.let { FutureMethodResult.result(it.x, it.y) } ?: FutureMethodResult.empty()

fun Vec3d?.toResult(): FutureMethodResult =
  this?.let { FutureMethodResult.result(it.x, it.y, it.z) } ?: FutureMethodResult.empty()
