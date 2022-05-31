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

public interface MultiPointResizable2d extends MultiPoint2d {
	int MAX_SIZE = 255;

	void removePoint(int idx);

	void addPoint(int idx, @Nonnull Vec2d point);

	BasicMethod<MultiPointResizable2d> GET_POINT_COUNT = BasicMethod.of(
		"getPointCount", "function():number -- Get the number of vertices on this object.",
		MultiPointResizable2d::getPointCount, false
	);
	static FutureMethodResult getPointCount(IUnbakedContext<MultiPointResizable2d> unbaked, IArguments args) throws LuaException {
		return FutureMethodResult.result(safeFromTarget(unbaked).getVertices());
	}

	BasicMethod<MultiPointResizable2d> REMOVE_POINT = BasicMethod.of(
		"removePoint", "function(idx:int) -- Remove the specified vertex of this object.",
		MultiPointResizable2d::removePoint, false
	);
	static FutureMethodResult removePoint(IUnbakedContext<MultiPointResizable2d> unbaked, IArguments args) throws LuaException {
		MultiPointResizable2d object = safeFromTarget(unbaked);
		int idx = assertBetween(args.getInt(0), 1, object.getVertices(), "Index out of range (%s)");
		object.removePoint(idx - 1);
		return FutureMethodResult.empty();
	}

	BasicMethod<MultiPointResizable2d> INSERT_POINT = BasicMethod.of(
		"insertPoint", "function([idx:int, ]x:number, y:number) -- Add a specified vertex to this object.",
		MultiPointResizable2d::insertPoint, false
	);
	static FutureMethodResult insertPoint(IUnbakedContext<MultiPointResizable2d> unbaked, IArguments args) throws LuaException {
		MultiPointResizable2d object = safeFromTarget(unbaked);

		if (object.getVertices() > MAX_SIZE) {
			throw new LuaException("Too many vertices");
		}

		int idx;
		double x, y;
		if (args.count() >= 3) {
			idx = assertBetween(args.getInt(0), 1, object.getVertices(), "Index out of range (%s)");
			x = args.getDouble(1);
			y = args.getDouble(2);
		} else {
			idx = object.getVertices();
			x = args.getDouble(0);
			y = args.getDouble(1);
		}

		object.addPoint(idx - 1, new Vec2d(x, y));

		return FutureMethodResult.empty();
	}
}
