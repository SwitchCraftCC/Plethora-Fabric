package io.sc3.plethora.api.method

import net.minecraft.util.math.Vec3d
import io.sc3.plethora.util.Vec2d

fun Vec2d?.toResult(): FutureMethodResult =
  this?.let { FutureMethodResult.result(it.x, it.y) } ?: FutureMethodResult.empty()

fun Vec3d?.toResult(): FutureMethodResult =
  this?.let { FutureMethodResult.result(it.x, it.y, it.z) } ?: FutureMethodResult.empty()
