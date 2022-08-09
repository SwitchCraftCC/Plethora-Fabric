package pw.switchcraft.plethora.gameplay.modules.glasses.canvas

import dan200.computercraft.api.lua.IArguments
import pw.switchcraft.plethora.api.method.*
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesArgumentHelper
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.getContext
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.DEFAULT_COLOUR
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Frame2d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Group2d
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.*

object Canvas2dMethods {
  val ADD_RECTANGLE = BasicMethod.of(
    "addRectangle", "function(x:number, y:number, width:number, height:number[, colour:number]):table -- Create a new rectangle.",
    { unbaked, args -> addRectangle(unbaked, args) }, false
  )
  private fun addRectangle(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    val pos = args.getVec2d(0)
    val size = args.getVec2d(2)
    val colour = args.optInt(4, DEFAULT_COLOUR.toInt())

    val rectangle = Rectangle(canvas.newObjectId(), group.id)
    rectangle.position = pos
    rectangle.size = size
    rectangle.colour = colour

    canvas.add(rectangle)
    return FutureMethodResult.result(ctx.context.makeChild(rectangle, canvas.reference(rectangle)).`object`)
  }

  val ADD_LINE = BasicMethod.of(
    "addLine", "function(start:table, end:table[, color:number][, thickness:number]):table -- Create a new line.",
    { unbaked, args -> addLine(unbaked, args) }, false
  )
  private fun addLine(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas
    
    val start = args.getVec2dTable(0)
    val end = args.getVec2dTable(1)
    val colour = args.optInt(2, DEFAULT_COLOUR.toInt())
    val thickness = args.optDouble(3, 1.0).toFloat()
    
    val line = Line(canvas.newObjectId(), group.id)
    line.setVertex(0, start)
    line.setVertex(1, end)
    line.colour = colour
    line.scale = thickness
    
    canvas.add(line)
    return FutureMethodResult.result(ctx.context.makeChild(line, canvas.reference(line)).`object`)
  }

  val ADD_DOT = BasicMethod.of(
    "addDot", "function(position:table[, color:number][, size:number]):table -- Create a new dot.",
    { unbaked, args -> addDot(unbaked, args) }, false
  )
  private fun addDot(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    val position = args.getVec2dTable(0)
    val colour = args.optInt(1, DEFAULT_COLOUR.toInt())
    val size = args.optDouble(2, 1.0).toFloat()

    val dot = Dot(canvas.newObjectId(), group.id)
    dot.position = position
    dot.colour = colour
    dot.scale = size

    canvas.add(dot)
    return FutureMethodResult.result(ctx.context.makeChild(dot, canvas.reference(dot)).`object`)
  }

  val ADD_TEXT = BasicMethod.of(
    "addText", "function(position:table, contents:string[, colour:number[, size:number]]):table -- Create a new text object.",
    { unbaked, args -> addText(unbaked, args) }, false
  )
  private fun addText(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    val position = args.getVec2dTable(0)
    val contents = args.getString(1)
    val colour = args.optInt(2, DEFAULT_COLOUR.toInt())
    val size = args.optDouble(3, 1.0).toFloat()

    val text = Text(canvas.newObjectId(), group.id)
    text.position = position
    text.text = contents
    text.colour = colour
    text.scale = size

    canvas.add(text)
    return FutureMethodResult.result(ctx.context.makeChild(text, canvas.reference(text)).`object`)
  }

  val ADD_TRIANGLE = BasicMethod.of(
    "addTriangle", "function(p1:table, p2:table, p3:table[, colour:number]):table -- Create a new triangle, composed of three points.",
    { unbaked, args -> addTriangle(unbaked, args) }, false
  )
  private fun addTriangle(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    val p1 = args.getVec2dTable(0)
    val p2 = args.getVec2dTable(1)
    val p3 = args.getVec2dTable(2)
    val colour = args.optInt(3, DEFAULT_COLOUR.toInt())

    val triangle = Triangle(canvas.newObjectId(), group.id)
    triangle.setVertex(0, p1)
    triangle.setVertex(1, p2)
    triangle.setVertex(2, p3)
    triangle.colour = colour

    canvas.add(triangle)
    return FutureMethodResult.result(ctx.context.makeChild(triangle, canvas.reference(triangle)).`object`)
  }

  val ADD_POLYGON = BasicMethod.of(
    "addPolygon", "function(points...:table[, color:number]):table -- Create a new polygon, composed of many points.",
    { unbaked, args -> addPolygon(unbaked, args) }, false
  )
  private fun addPolygon(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    // TODO: Unlike the other add methods, this one will allocate an object ID even if argument validation fails
    val polygon = Polygon(canvas.newObjectId(), group.id)
    val i = polygon.addPointsFromArgs(args)
    polygon.colour = args.optInt(i, DEFAULT_COLOUR.toInt())

    canvas.add(polygon)
    return FutureMethodResult.result(ctx.context.makeChild(polygon, canvas.reference(polygon)).`object`)
  }

  val ADD_LINES = BasicMethod.of(
    "addLines", "function(points...:table[, color:number[, thickness:number]]):table -- Create a new line loop, composed of many points.",
    { unbaked, args -> addLines(unbaked, args) }, false
  )
  private fun addLines(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    // TODO: Unlike the other add methods, this one will allocate an object ID even if argument validation fails
    val lines = LineLoop(canvas.newObjectId(), group.id)
    val i = lines.addPointsFromArgs(args)
    lines.colour = args.optInt(i, DEFAULT_COLOUR.toInt())
    lines.scale = args.optDouble(i + 1, 1.0).toFloat()

    canvas.add(lines)
    return FutureMethodResult.result(ctx.context.makeChild(lines, canvas.reference(lines)).`object`)
  }

  val ADD_ITEM = BasicMethod.of(
    "addItem", "function(position:table, contents:string[, scale:number]):table -- Create an item icon.",
    { unbaked, args -> addItem(unbaked, args) }, false
  )
  private fun addItem(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    val position = args.getVec2dTable(0)
    val item = GlassesArgumentHelper.getItem(args, 1)
    val scale = args.optDouble(2, 1.0).toFloat()

    val model = Item2d(canvas.newObjectId(), group.id)
    model.position = position
    model.scale = scale
    model.item = item

    canvas.add(model)
    return FutureMethodResult.result(ctx.context.makeChild(model, canvas.reference(model)).`object`)
  }

  val ADD_GROUP = BasicMethod.of(
    "addGroup", "function(position:table):table -- Create a new object group.",
    { unbaked, args -> addGroup(unbaked, args) }, false
  )
  private fun addGroup(unbaked: IUnbakedContext<Group2d>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked, Group2d::class.java)
    val group = ctx.target
    val canvas = ctx.canvas

    val position = args.getVec2dTable(0)

    val newGroup = ObjectGroup2d(canvas.newObjectId(), group.id)
    newGroup.position = position

    canvas.add(newGroup)
    return FutureMethodResult.result(ctx.context.makeChild(newGroup, canvas.reference(newGroup)).`object`)
  }

  val GET_SIZE = BasicMethod.of(
    "getSize", "function():number, number -- Get the size of this canvas.",
    { unbaked, _ -> getSize(unbaked) }, false
  )
  private fun getSize(unbaked: IUnbakedContext<Frame2d>): FutureMethodResult {
    val ctx = getContext(unbaked, Frame2d::class.java)
    val target = ctx.target
    return FutureMethodResult.result(target.width, target.height)
  }
}
