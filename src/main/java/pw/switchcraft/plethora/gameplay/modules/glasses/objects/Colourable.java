package pw.switchcraft.plethora.gameplay.modules.glasses.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;

import static pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget;

/**
 * An object which can be coloured.
 */
public interface Colourable {
	int DEFAULT_COLOUR = 0xFFFFFFFF;

	int getColour();

	void setColour(int colour);

	BasicMethod<Colourable> GET_COLOUR = BasicMethod.of(
		"getColour", "function():number -- Get the colour for this object.",
		Colourable::getColour, false
	);
	BasicMethod<Colourable> GET_COLOR = BasicMethod.alias(GET_COLOUR, "getColor");
	static FutureMethodResult getColour(IUnbakedContext<Colourable> unbaked, IArguments args) throws LuaException {
		return FutureMethodResult.result(safeFromTarget(unbaked).getColour() & 0xFFFFFFFFL);
	}

	BasicMethod<Colourable> SET_COLOUR = BasicMethod.of(
		"setColour", "function(colour|r:int, [g:int, b:int], [alpha:int]):number -- Set the colour for this object.",
		Colourable::setColour, false
	);
	BasicMethod<Colourable> SET_COLOR = BasicMethod.alias(SET_COLOUR, "setColor");
	static FutureMethodResult setColour(IUnbakedContext<Colourable> unbaked, IArguments args) throws LuaException {
		Colourable object = safeFromTarget(unbaked);

		switch (args.count()) {
			case 1:
				object.setColour(args.getInt(0));
				break;

			case 3: {
				int r = args.getInt(0) & 0xFF;
				int g = args.getInt(1) & 0xFF;
				int b = args.getInt(2) & 0xFF;
				object.setColour((r << 24) | (g << 16) | (b << 8) | object.getColour() & 0xFF);
				break;
			}

			default:
			case 4: {
				int r = args.getInt(0) & 0xFF;
				int g = args.getInt(1) & 0xFF;
				int b = args.getInt(2) & 0xFF;
				int a = args.getInt(3) & 0xFF;
				object.setColour((r << 24) | (g << 16) | (b << 8) | a);
				break;
			}
		}

		return FutureMethodResult.empty();
	}

	BasicMethod<Colourable> GET_ALPHA = BasicMethod.of(
		"getAlpha", "function():number -- Get the alpha for this object.",
		Colourable::getAlpha, false
	);
	static FutureMethodResult getAlpha(IUnbakedContext<Colourable> unbaked, IArguments args) throws LuaException {
		return FutureMethodResult.result(safeFromTarget(unbaked).getColour() & 0xFF);
	}

	BasicMethod<Colourable> SET_ALPHA = BasicMethod.of(
		"setAlpha", "function(alpha:number):number -- Set the alpha for this object.",
		Colourable::setAlpha, false
	);
	static FutureMethodResult setAlpha(IUnbakedContext<Colourable> unbaked, IArguments args) throws LuaException {
		Colourable object = safeFromTarget(unbaked);
		object.setColour((object.getColour() & ~0xFF) | (args.getInt(0) & 0xFF));
		return FutureMethodResult.empty();
	}
}
