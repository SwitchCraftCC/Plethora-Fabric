package pw.switchcraft.plethora.api.method

import net.minecraft.util.math.Vec3d

fun Vec3d?.toResult(): FutureMethodResult =
  this?.let { FutureMethodResult.result(it.x, it.y, it.z) } ?: FutureMethodResult.empty()
