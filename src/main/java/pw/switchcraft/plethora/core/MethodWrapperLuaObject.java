package pw.switchcraft.plethora.core;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.ObjectUtils;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.api.method.TypedLuaObject;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Handles integration with a {@link dan200.computercraft.api.lua.IDynamicLuaObject}.
 */
public class MethodWrapperLuaObject<T> extends MethodWrapper implements TypedLuaObject<T> {
	public MethodWrapperLuaObject(List<RegisteredMethod<?>> methods, List<UnbakedContext<?>> contexts) {
		super(methods, contexts);
	}

	public MethodWrapperLuaObject(Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> contexts) {
		super(contexts.getLeft(), contexts.getRight());
	}

	@Override
	public MethodResult callMethod(@Nonnull ILuaContext luaContext, int method, @Nonnull final IArguments args) throws LuaException {
		UnbakedContext<?> context = getContext(method);
		@SuppressWarnings("unchecked")
		FutureMethodResult result = getMethod(method).call((IUnbakedContext) context, args);
		return ObjectUtils.firstNonNull(context.getExecutor().execute(result, luaContext), MethodResult.of());

//		RegisteredMethod<?> registeredMethod = getMethod(method);
//		return luaContext.executeMainThreadTask(() -> registeredMethod.call((IUnbakedContext) context, args)
//			.getResult());
	}
}
