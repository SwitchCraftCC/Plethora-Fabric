package pw.switchcraft.plethora.gameplay.modules.glasses.canvas;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.TargetedGlassesContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Frame2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup.Group2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.ObjectGroup2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.Rectangle;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.modules.glasses.ArgumentPointHelper.getVec2d;
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

    // TODO: addLine
    // TODO: addDot
    // TODO: addText
    // TODO: addTriangle
    // TODO: addPolygon
    // TODO: addLines
    // TODO: addItem
}
