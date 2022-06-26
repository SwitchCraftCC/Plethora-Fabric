package pw.switchcraft.plethora.gameplay.modules.glasses;

import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.IContext;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.core.ContextHelpers;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasServer;

import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.GLASSES_S;

public class GlassesMethodsHelpers {
    public record GlassesContext(IContext<IModuleContainer> context, CanvasServer server) {}
    public static GlassesContext getContext(IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IContext<IModuleContainer> context = unbaked.safeBake();
        CanvasServer server = ContextHelpers.fromSubtarget(context, CanvasServer.class, GLASSES_S);
        return new GlassesContext(context, server);
    }

    public record TargetedGlassesContext<T>(IContext<T> context, T target, Class<T> targetCls, CanvasServer canvas) {}
    public static <T> TargetedGlassesContext<T> getContext(IUnbakedContext<T> unbaked, Class<T> targetCls) throws LuaException {
        IContext<T> context = unbaked.safeBake();
        T target = ContextHelpers.fromTarget(context);
        CanvasServer canvas = ContextHelpers.fromContext(context, CanvasServer.class, GLASSES_S);
        return new TargetedGlassesContext<>(context, target, targetCls, canvas);
    }
}
