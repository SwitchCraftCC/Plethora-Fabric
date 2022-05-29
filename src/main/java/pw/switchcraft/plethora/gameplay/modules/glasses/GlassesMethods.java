package pw.switchcraft.plethora.gameplay.modules.glasses;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.SubtargetedModuleMethod;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.GlassesContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.TargetedGlassesContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasServer;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethodsHelpers.getContext;
import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.GLASSES_M;

public final class GlassesMethods {
    public static final SubtargetedModuleMethod<CanvasServer> GET_CANVAS = SubtargetedModuleMethod.of(
        "canvas", GLASSES_M, CanvasServer.class,
        "function():table -- Get the 2D canvas for these glasses.",
        GlassesMethods::canvas, false
    );
    public static FutureMethodResult canvas(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        GlassesContext ctx = getContext(unbaked);
        return FutureMethodResult.result(ctx.context().makeChildId(ctx.server().canvas2d()).getObject());
    }

    public static final SubtargetedModuleMethod<CanvasServer> GET_CANVAS_3D = SubtargetedModuleMethod.of(
        "canvas3d", GLASSES_M, CanvasServer.class,
        "function():table -- Get the 3D canvas for these glasses.",
        GlassesMethods::canvas3d, false
    );
    public static FutureMethodResult canvas3d(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                              @Nonnull IArguments args) throws LuaException {
        GlassesContext ctx = getContext(unbaked);
        return FutureMethodResult.result(ctx.context().makeChildId(ctx.server().canvas3d()).getObject());
    }

    public static final BasicMethod<ObjectGroup> CLEAR = BasicMethod.of(
        "clear", "function() -- Remove all objects.", GlassesMethods::clear, false
    );
    public static FutureMethodResult clear(@Nonnull IUnbakedContext<ObjectGroup> unbaked,
                                           @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<ObjectGroup> ctx = getContext(unbaked, ObjectGroup.class);
        ctx.canvas().clear(ctx.target());
        return FutureMethodResult.empty();
    }

    public static final BasicMethod<BaseObject> REMOVE = BasicMethod.of(
        "remove", "function() -- Remove this object from the canvas.", GlassesMethods::remove, false
    );
    public static FutureMethodResult remove(@Nonnull IUnbakedContext<BaseObject> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        TargetedGlassesContext<BaseObject> ctx = getContext(unbaked, BaseObject.class);
        ctx.canvas().remove(ctx.target());
        return FutureMethodResult.empty();
    }
}
