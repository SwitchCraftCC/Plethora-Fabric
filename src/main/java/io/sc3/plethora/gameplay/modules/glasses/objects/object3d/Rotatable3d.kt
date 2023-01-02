package io.sc3.plethora.gameplay.modules.glasses.objects.object3d

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.*
import io.sc3.plethora.core.ContextHelpers.safeFromTarget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d

interface Rotatable3d {
  var rotation: Vec3d?

  fun applyRotation(matrices: MatrixStack) {
    val mc = MinecraftClient.getInstance()

    val rot = rotation
    if (rot == null) {
      val cam = mc.gameRenderer.camera
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - cam.yaw))
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cam.pitch))
    } else {
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rot.x.toFloat()))
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot.y.toFloat()))
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rot.z.toFloat()))
    }
  }

  companion object {
    val GET_ROTATION = BasicMethod.of(
      "getRotation", "function():nil|number, number, number -- Get the rotation for this object, or nil if it faces the player.",
      { unbaked, _ -> getRotation(unbaked) }, false
    )
    private fun getRotation(unbaked: IUnbakedContext<Rotatable3d>) =
      safeFromTarget(unbaked).rotation.toResult()

    val SET_ROTATION = BasicMethod.of(
      "setRotation", "function([x:number, y:number, z:number]) -- Set the rotation for this object, passing nothing if it should face the player.",
      { unbaked, args -> setRotation(unbaked, args) }, false
    )
    private fun setRotation(unbaked: IUnbakedContext<Rotatable3d>, args: IArguments): FutureMethodResult {
      safeFromTarget(unbaked).rotation = args.getVec3dNullable(0)
      return FutureMethodResult.empty()
    }
  }
}
