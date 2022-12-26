package io.sc3.plethora.gameplay.modules.glasses

import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.SubtargetedModuleMethod
import io.sc3.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.getContext
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasServer
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectGroup
import io.sc3.plethora.gameplay.registry.PlethoraModules.GLASSES_M

object GlassesMethods {
  val GET_CANVAS = SubtargetedModuleMethod.of(
    "canvas", GLASSES_M, CanvasServer::class.java,
    "function():table -- Get the 2D canvas for these glasses.",
    { unbaked, _ -> canvas(unbaked) }, false
  )
  private fun canvas(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val ctx = getContext(unbaked)
    return FutureMethodResult.result(ctx.context.makeChildId(ctx.server().canvas2d).`object`)
  }

  val GET_CANVAS_3D = SubtargetedModuleMethod.of(
    "canvas3d", GLASSES_M, CanvasServer::class.java,
    "function():table -- Get the 3D canvas for these glasses.",
    { unbaked, _ -> canvas3d(unbaked) }, false
  )
  private fun canvas3d(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val ctx = getContext(unbaked)
    return FutureMethodResult.result(ctx.context.makeChildId(ctx.server().canvas3d).`object`)
  }

  val CLEAR = BasicMethod.of(
    "clear", "function() -- Remove all objects.",
    { unbaked, _ -> clear(unbaked) }, false
  )
  private fun clear(unbaked: IUnbakedContext<ObjectGroup>): FutureMethodResult {
    val ctx = getContext(unbaked, ObjectGroup::class.java)
    ctx.canvas.clear(ctx.target)
    return FutureMethodResult.empty()
  }

  val REMOVE = BasicMethod.of(
    "remove", "function() -- Remove this object from the canvas.",
    { unbaked, _ -> remove(unbaked) }, false
  )
  private fun remove(unbaked: IUnbakedContext<BaseObject>): FutureMethodResult {
    val ctx = getContext(unbaked, BaseObject::class.java)
    ctx.canvas.remove(ctx.target)
    return FutureMethodResult.empty()
  }
}
