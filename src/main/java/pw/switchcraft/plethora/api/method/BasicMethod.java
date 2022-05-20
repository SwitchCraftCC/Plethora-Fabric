package pw.switchcraft.plethora.api.method;

import com.google.common.base.Strings;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import javax.annotation.Nonnull;

/**
 * A basic wrapper for methods
 */
public abstract class BasicMethod<T> implements IMethod<T> {
	private final String name;
	private final String docs;
	private final int priority;

	public BasicMethod(String name, String docs) {
		this(name, 0, docs);
	}

	public BasicMethod(String name, int priority, String docs) {
		this.name = name;
		this.priority = priority;
		this.docs = Strings.isNullOrEmpty(docs) ? null : docs;
	}

	public static <T> BasicMethod<T> of(String name, String docs, Delegate<T> delegate, boolean worldThread) {
		return new BasicMethod<>(name, docs) {
			@Nonnull
			@Override
			public FutureMethodResult apply(@Nonnull IUnbakedContext<T> context,
											@Nonnull IArguments args) throws LuaException {
				return worldThread
					? FutureMethodResult.nextTick(() -> delegate.apply(context, args))
					: delegate.apply(context, args);
			}
		};
	}

	public static <T> BasicMethod<T> of(String name, String docs, Delegate<T> delegate) {
		return of(name, docs, delegate, true);
	}

	@Nonnull
	@Override
	public final String getName() {
		return name;
	}

	@Override
	public boolean canApply(@Nonnull IPartialContext<T> context) {
		return true;
	}

	@Override
	public final int getPriority() {
		return priority;
	}

	@Nonnull
	@Override
	public String getDocString() {
		return docs;
	}
}
