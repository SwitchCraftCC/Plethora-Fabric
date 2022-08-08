package pw.switchcraft.plethora.gameplay.modules.glasses.canvas

import dan200.computercraft.api.lua.IArguments
import net.minecraft.util.math.Vec3d
import pw.switchcraft.plethora.api.method.*
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.getContext
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Colourable.DEFAULT_COLOUR
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Group3d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Origin3d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d.Box
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d.ObjectRoot3d

object Canvas3dMethods {
  @JvmField
  val CREATE = BasicMethod.of(
    "create", "function([offsetX: number, offsetY: number, offsetZ: number]) -- Create a new 3D canvas centred relative to the current position.",
    { unbaked, args -> create(unbaked, args) }, false
  )
  private fun create(unbaked: IUnbakedContext<Origin3d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Origin3d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas
    val location = ctx.location

    val offset = args.getVec3dNullable(0) ?: Vec3d.ZERO

    val root = ObjectRoot3d(canvas.newObjectId(), group.id())
    root.recenter(location.world, location.loc.add(offset))

    canvas.add(root)

    return FutureMethodResult.result(ctx.context.makeChild(root, canvas.reference(root)).`object`)
  }

  // TODO: addFrame

  @JvmField
  val ADD_BOX = BasicMethod.of(
    "addBox", "function(function(x:number, y:number, z:number[, width:number, height:number, depth:number][, color:number]):table -- Create a new box.",
    { unbaked, args -> addBox(unbaked, args) }, false
  )
  private fun addBox(unbaked: IUnbakedContext<Group3d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group3d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    val pos = args.getVec3d(0)
    val colour = args.optInt(if (args.count() <= 4) 3 else 6, DEFAULT_COLOUR)
    val size = if (args.count() <= 4) Vec3d(1.0, 1.0, 1.0) else args.getVec3d(3)

    val box = Box(canvas.newObjectId(), group.id())
    box.position = pos
    box.size = size
    box.colour = colour

    canvas.add(box)

    return FutureMethodResult.result(ctx.context.makeChild(box, canvas.reference(box)).`object`)
  }

  // TODO: addLine
  // TODO: addItem
}
