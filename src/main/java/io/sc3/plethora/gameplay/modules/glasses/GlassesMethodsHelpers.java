package io.sc3.plethora.gameplay.modules.glasses;

import dan200.computercraft.api.lua.LuaException;
import io.sc3.plethora.api.IWorldLocation;
import io.sc3.plethora.api.method.ContextKeys;
import io.sc3.plethora.api.method.IContext;
import io.sc3.plethora.api.method.IUnbakedContext;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.core.ContextHelpers;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasServer;

import static io.sc3.plethora.gameplay.registry.PlethoraModules.GLASSES_S;

public class GlassesMethodsHelpers {
  public record GlassesContext(
    IContext<IModuleContainer> context,
    CanvasServer server,
    IWorldLocation location
  ) {}

  public static GlassesContext getContext(IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
    IContext<IModuleContainer> context = unbaked.safeBake();
    CanvasServer server = ContextHelpers.fromSubtarget(context, CanvasServer.class, GLASSES_S);
    IWorldLocation location = context.getContext(ContextKeys.ORIGIN, IWorldLocation.class);
    return new GlassesContext(context, server, location);
  }

  public record TargetedGlassesContext<T>(
    IContext<T> context,
    T target,
    Class<T> targetCls,
    CanvasServer canvas,
    IWorldLocation location
  ) {}

  public static <T> TargetedGlassesContext<T> getContext(IUnbakedContext<T> unbaked, Class<T> targetCls) throws LuaException {
    IContext<T> context = unbaked.safeBake();
    T target = ContextHelpers.fromTarget(context);
    CanvasServer canvas = ContextHelpers.fromContext(context, CanvasServer.class, GLASSES_S);
    IWorldLocation location = context.getContext(ContextKeys.ORIGIN, IWorldLocation.class);
    return new TargetedGlassesContext<>(context, target, targetCls, canvas, location);
  }
}
