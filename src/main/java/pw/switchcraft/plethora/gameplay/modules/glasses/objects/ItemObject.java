package pw.switchcraft.plethora.gameplay.modules.glasses.objects;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesArgumentHelper;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget;

/**
 * An object which contains an item.
 */
public interface ItemObject {
	@Nonnull
	Item getItem();

	void setItem(@Nonnull Item item);

	BasicMethod<ItemObject> GET_ITEM = BasicMethod.of(
		"getItem", "function():string -- Get the item for this object.",
		ItemObject::getItem, false
	);
	static FutureMethodResult getItem(IUnbakedContext<ItemObject> unbaked, IArguments args) throws LuaException {
		Item item = safeFromTarget(unbaked).getItem();
		Identifier id = Registry.ITEM.getId(item);
		return FutureMethodResult.result(id.toString());
	}

	BasicMethod<ItemObject> SET_ITEM = BasicMethod.of(
		"setItem", "function(string) -- Set the item for this object.",
		ItemObject::setItem, false
	);
	static FutureMethodResult setItem(IUnbakedContext<ItemObject> unbaked, IArguments args) throws LuaException {
		safeFromTarget(unbaked).setItem(GlassesArgumentHelper.getItem(args, 0));
		return FutureMethodResult.empty();
	}
}
