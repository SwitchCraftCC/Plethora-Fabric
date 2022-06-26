package pw.switchcraft.plethora.gameplay.modules.glasses.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.api.method.ArgumentHelper.assertBetween;
import static pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget;

/**
 * An object which contains text.
 */
public interface TextObject {
	@Nonnull
	String getText();

	void setText(@Nonnull String text);

	void setShadow(boolean dropShadow);

	boolean hasShadow();

	void setLineHeight(short height);

	short getLineHeight();

	BasicMethod<TextObject> GET_TEXT = BasicMethod.of(
		"getText", "function():string -- Get the text for this object.",
		TextObject::getText, false
	);
	static FutureMethodResult getText(IUnbakedContext<TextObject> unbaked, IArguments args) throws LuaException {
		return FutureMethodResult.result(safeFromTarget(unbaked).getText());
	}

	BasicMethod<TextObject> SET_TEXT = BasicMethod.of(
		"setText", "function(string) -- Set the text for this object.",
		TextObject::setText, false
	);
	static FutureMethodResult setText(IUnbakedContext<TextObject> unbaked, IArguments args) throws LuaException {
		String contents = args.getString(0);
		assertBetween(contents.length(), 0, 512, "string length out of bounds (%s)");
		safeFromTarget(unbaked).setText(contents);
		return FutureMethodResult.empty();
	}

	BasicMethod<TextObject> SET_SHADOW = BasicMethod.of(
		"setShadow", "function(boolean) -- Set the shadow for this object.",
		TextObject::setShadow, false
	);
	static FutureMethodResult setShadow(IUnbakedContext<TextObject> unbaked, IArguments args) throws LuaException {
		safeFromTarget(unbaked).setShadow(args.getBoolean(0));
		return FutureMethodResult.empty();
	}

	BasicMethod<TextObject> HAS_SHADOW = BasicMethod.of(
		"hasShadow", "function():boolean -- Get the shadow for this object.",
		TextObject::hasShadow, false
	);
	static FutureMethodResult hasShadow(IUnbakedContext<TextObject> unbaked, IArguments args) throws LuaException {
		return FutureMethodResult.result(safeFromTarget(unbaked).hasShadow());
	}

	BasicMethod<TextObject> GET_LINE_HEIGHT = BasicMethod.of(
		"getLineHeight", "function():number -- Get the line height for this object.",
		TextObject::getLineHeight, false
	);
	static FutureMethodResult getLineHeight(IUnbakedContext<TextObject> unbaked, IArguments args) throws LuaException {
		return FutureMethodResult.result(safeFromTarget(unbaked).getLineHeight());
	}

	BasicMethod<TextObject> SET_LINE_HEIGHT = BasicMethod.of(
		"setLineHeight", "function(number) -- Set the line height for this object.",
		TextObject::setLineHeight, false
	);
	static FutureMethodResult setLineHeight(IUnbakedContext<TextObject> unbaked, IArguments args) throws LuaException {
		safeFromTarget(unbaked).setLineHeight((short) args.getInt(0));
		return FutureMethodResult.empty();
	}
}
