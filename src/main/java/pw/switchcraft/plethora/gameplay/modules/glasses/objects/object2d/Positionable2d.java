package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget;

/**
 * An object which can be positioned in 2D.
 */
public interface Positionable2d {
    @Nonnull
    Vec2d getPosition();

    void setPosition(@Nonnull Vec2d position);

    BasicMethod<Positionable2d> GET_POSITION = BasicMethod.of(
        "getPosition", "function():number, number -- Get the position for this object.",
        Positionable2d::getPosition, false
    );
    static FutureMethodResult getPosition(IUnbakedContext<Positionable2d> unbaked, IArguments args) throws LuaException {
        Vec2d pos = safeFromTarget(unbaked).getPosition();
        return FutureMethodResult.result(pos.x(), pos.y());
    }

    BasicMethod<Positionable2d> SET_POSITION = BasicMethod.of(
        "setPosition", "function(number, number) -- Set the position for this object.",
        Positionable2d::setPosition, false
    );
    static FutureMethodResult setPosition(IUnbakedContext<Positionable2d> unbaked, IArguments args) throws LuaException {
        double x = args.getDouble(0), y = args.getDouble(1);
        safeFromTarget(unbaked).setPosition(new Vec2d(x, y));
        return FutureMethodResult.empty();
    }
}
