package pw.switchcraft.plethora.gameplay.modules.glasses.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;

import static pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget;

/**
 * An object which can be scaled. This includes point side, text size and line thickness.
 */
public interface Scalable {
	float getScale();

	void setScale(float scale);

	BasicMethod<Scalable> GET_SCALE = BasicMethod.of(
		"getScale", "function():number -- Get the scale for this object.",
		Scalable::getScale, false
	);
	static FutureMethodResult getScale(IUnbakedContext<Scalable> unbaked, IArguments args) throws LuaException {
		return FutureMethodResult.result(safeFromTarget(unbaked).getScale());
	}

	BasicMethod<Scalable> SET_SCALE = BasicMethod.of(
		"setScale", "function(number) -- Set the scale for this object.",
		Scalable::setScale, false
	);
	static FutureMethodResult setScale(IUnbakedContext<Scalable> unbaked, IArguments args) throws LuaException {
		float scale = (float) args.getDouble(0);
		if (scale <= 0) throw new LuaException("Scale must be > 0");
		safeFromTarget(unbaked).setScale(scale);
		return FutureMethodResult.empty();
	}
}
