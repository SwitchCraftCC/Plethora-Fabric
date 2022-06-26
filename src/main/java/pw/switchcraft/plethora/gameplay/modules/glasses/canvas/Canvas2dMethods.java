package pw.switchcraft.plethora.gameplay.modules.glasses.canvas;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.item.Item;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesArgumentHelper;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.TargetedGlassesContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Frame2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Group2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.*;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.modules.glasses.GlassesArgumentHelper.getVec2d;
import static pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.getContext;
import static pw.switchcraft.plethora.gameplay.modules.glasses.objects.Colourable.DEFAULT_COLOUR;

public final class Canvas2dMethods {
    public static final BasicMethod<Group2d> ADD_RECTANGLE = BasicMethod.of(
        "addRectangle",
        "function(x:number, y:number, width:number, height:number[, colour:number]):table -- Create a new rectangle.",
        Canvas2dMethods::addRectangle, false
    );
    public static FutureMethodResult addRectangle(@Nonnull IUnbakedContext<Group2d> unbaked,
                                                  @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        float x = (float) args.getDouble(0), y = (float) args.getDouble(1),
              width = (float) args.getDouble(2), height = (float) args.getDouble(3);
        int colour = args.optInt(4, DEFAULT_COLOUR);

        Rectangle rectangle = new Rectangle(canvas.newObjectId(), group.id());
        rectangle.setPosition(new Vec2d(x, y));
        rectangle.setSize(width, height);
        rectangle.setColour(colour);

        canvas.add(rectangle);

        return FutureMethodResult.result(ctx.context().makeChild(rectangle, canvas.reference(rectangle)).getObject());
    }

    public static final BasicMethod<Group2d> ADD_LINE = BasicMethod.of(
        "addLine",
        "function(start:table, end:table[, color:number][, thickness:number]):table -- Create a new line.",
        Canvas2dMethods::addLine, false
    );
    public static FutureMethodResult addLine(@Nonnull IUnbakedContext<Group2d> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        Vec2d start = getVec2d(args, 0), end = getVec2d(args, 1);
        int colour = args.optInt(2, DEFAULT_COLOUR);
        float thickness = (float) args.optDouble(3, 1);

        Line line = new Line(canvas.newObjectId(), group.id());
        line.setVertex(0, start);
        line.setVertex(1, end);
        line.setColour(colour);
        line.setScale(thickness);

        canvas.add(line);

        return FutureMethodResult.result(ctx.context().makeChild(line, canvas.reference(line)).getObject());
    }
    
    public static final BasicMethod<Group2d> ADD_DOT = BasicMethod.of(
        "addDot",
        "function(position:table[, color:number][, size:number]):table -- Create a new dot.",
        Canvas2dMethods::addDot, false
    );
    public static FutureMethodResult addDot(@Nonnull IUnbakedContext<Group2d> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        Vec2d position = getVec2d(args, 0);
        int colour = args.optInt(1, DEFAULT_COLOUR);
        float size = (float) args.optDouble(2, 1);

        Dot dot = new Dot(canvas.newObjectId(), group.id());
        dot.setPosition(position);
        dot.setColour(colour);
        dot.setScale(size);

        canvas.add(dot);
        return FutureMethodResult.result(ctx.context().makeChild(dot, canvas.reference(dot)).getObject());
    }

    public static final BasicMethod<Group2d> ADD_TEXT = BasicMethod.of(
        "addText",
        "function(position:table, contents:string[, colour:number[, size:number]]):table -- Create a new text object.",
        Canvas2dMethods::addText, false
    );
    public static FutureMethodResult addText(@Nonnull IUnbakedContext<Group2d> unbaked,
                                             @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        Vec2d position = getVec2d(args, 0);
        String contents = args.getString(1);
        int colour = args.optInt(2, DEFAULT_COLOUR);
        float size = (float) args.optDouble(3, 1);

        Text text = new Text(canvas.newObjectId(), group.id());
        text.setPosition(position);
        text.setText(contents);
        text.setColour(colour);
        text.setScale(size);

        canvas.add(text);

        return FutureMethodResult.result(ctx.context().makeChild(text, canvas.reference(text)).getObject());
    }
    
    public static final BasicMethod<Group2d> ADD_TRIANGLE = BasicMethod.of(
        "addTriangle",
        "function(p1:table, p2:table, p3:table[, colour:number]):table -- Create a new triangle, composed of three points.",
        Canvas2dMethods::addTriangle, false
    );
    public static FutureMethodResult addTriangle(@Nonnull IUnbakedContext<Group2d> unbaked,
                                                 @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        Vec2d p1 = getVec2d(args, 0), p2 = getVec2d(args, 1), p3 = getVec2d(args, 2);
        int colour = args.optInt(3, DEFAULT_COLOUR);

        Triangle triangle = new Triangle(canvas.newObjectId(), group.id());
        triangle.setVertex(0, p1);
        triangle.setVertex(1, p2);
        triangle.setVertex(2, p3);
        triangle.setColour(colour);

        canvas.add(triangle);

        return FutureMethodResult.result(ctx.context().makeChild(triangle, canvas.reference(triangle)).getObject());
    }
    
    public static final BasicMethod<Group2d> ADD_POLYGON = BasicMethod.of(
        "addPolygon",
        "function(points...:table[, color:number]):table -- Create a new polygon, composed of many points.",
        Canvas2dMethods::addPolygon, false
    );
    public static FutureMethodResult addPolygon(@Nonnull IUnbakedContext<Group2d> unbaked,
                                                @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        Polygon polygon = new Polygon(canvas.newObjectId(), group.id());
        int i;
        for (i = 0; i < args.count(); i++) {
            Object arg = args.get(i);
            if (i >= args.count() - 1 && arg instanceof Number) {
                break;
            } else {
                polygon.addPoint(i, getVec2d(args, i));
            }
        }

        polygon.setColour(args.optInt(i, DEFAULT_COLOUR));

        canvas.add(polygon);
        return FutureMethodResult.result(ctx.context().makeChild(polygon, canvas.reference(polygon)).getObject());
    }
    
    public static final BasicMethod<Group2d> ADD_LINES = BasicMethod.of(
        "addLines",
        "function(points...:table[, color:number[, thickness:number]]):table -- Create a new line loop, composed of many points.",
        Canvas2dMethods::addLines, false
    );
    public static FutureMethodResult addLines(@Nonnull IUnbakedContext<Group2d> unbaked,
                                              @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        LineLoop lines = new LineLoop(canvas.newObjectId(), group.id());
        int i;
        for (i = 0; i < args.count(); i++) {
            Object arg = args.get(i);
            if (i >= args.count() - 1 && arg instanceof Number) {
                break;
            } else {
                lines.addPoint(i, getVec2d(args, i));
            }
        }

        lines.setColour(args.optInt(i, DEFAULT_COLOUR));
        lines.setScale((float) args.optDouble(i + 1, 1.0));

        canvas.add(lines);
        return FutureMethodResult.result(ctx.context().makeChild(lines, canvas.reference(lines)).getObject());
    }

    public static final BasicMethod<Group2d> ADD_ITEM = BasicMethod.of(
        "addItem",
        "function(position:table, contents:string[, scale:number]):table -- Create an item icon.",
        Canvas2dMethods::addItem, false
    );
    public static FutureMethodResult addItem(@Nonnull IUnbakedContext<Group2d> unbaked,
                                             @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        Vec2d position = getVec2d(args, 0);
        Item item = GlassesArgumentHelper.getItem(args, 1);
        float size = (float) args.optDouble(2, 1);

        Item2d model = new Item2d(canvas.newObjectId(), group.id());
        model.setPosition(position);
        model.setScale(size);
        model.setItem(item);

        canvas.add(model);

        return FutureMethodResult.result(ctx.context().makeChild(model, canvas.reference(model)).getObject());
    }

    public static final BasicMethod<Group2d> ADD_GROUP = BasicMethod.of(
        "addGroup",
        "function(position:table):table -- Create a new object group.",
        Canvas2dMethods::addGroup, false
    );
    public static FutureMethodResult addGroup(@Nonnull IUnbakedContext<Group2d> unbaked,
                                              @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Group2d> ctx = getContext(unbaked, Group2d.class);
        Group2d group = ctx.target();
        CanvasServer canvas = ctx.canvas();

        Vec2d position = getVec2d(args, 0);

        ObjectGroup2d newGroup = new ObjectGroup2d(canvas.newObjectId(), group.id());
        newGroup.setPosition(position);

        canvas.add(newGroup);

        return FutureMethodResult.result(ctx.context().makeChild(newGroup, canvas.reference(newGroup)).getObject());
    }
    
    public static final BasicMethod<Frame2d> GET_SIZE = BasicMethod.of(
        "getSize", "function():number, number -- Get the size of this canvas.",
        Canvas2dMethods::getSize, false
    );
    public static FutureMethodResult getSize(@Nonnull IUnbakedContext<Frame2d> unbaked,
                                             @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<Frame2d> ctx = getContext(unbaked, Frame2d.class);
        Frame2d target = ctx.target();
        return FutureMethodResult.result(target.getWidth(), target.getHeight());
    }
}
