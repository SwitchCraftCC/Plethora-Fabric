package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.api.method.ArgumentHelper.assertBetween;
import static pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget;

/**
 * A polygon for which you can set multiple points.
 */
public interface MultiPoint2d {
	@Nonnull
	Vec2d getPoint(int idx);

	void setVertex(int idx, @Nonnull Vec2d point);

	int getVertices();

	BasicMethod<MultiPoint2d> GET_POINT = BasicMethod.of(
		"getPoint", "function(idx:int):number, number -- Get the specified vertex of this object.",
		MultiPoint2d::getPoint, false
	);
	static FutureMethodResult getPoint(IUnbakedContext<MultiPoint2d> unbaked, IArguments args) throws LuaException {
		MultiPoint2d object = safeFromTarget(unbaked);
		int idx = assertBetween(args.getInt(0), 1, object.getVertices(), "Index out of range (%s)");
		Vec2d point = object.getPoint(idx - 1);
		return FutureMethodResult.result(point.x(), point.y());
	}

	BasicMethod<MultiPoint2d> SET_POINT = BasicMethod.of(
		"setPoint", "function(idx:int, x:number, y:number) -- Set the specified vertex of this object.",
		MultiPoint2d::setPoint, false
	);
	static FutureMethodResult setPoint(IUnbakedContext<MultiPoint2d> unbaked, IArguments args) throws LuaException {
		MultiPoint2d object = safeFromTarget(unbaked);
		int idx = assertBetween(args.getInt(0), 1, object.getVertices(), "Index out of range (%s)");
		double x = args.getDouble(1), y = args.getDouble(2);
		object.setVertex(idx - 1, new Vec2d(x, y));
		return FutureMethodResult.empty();
	}
}
