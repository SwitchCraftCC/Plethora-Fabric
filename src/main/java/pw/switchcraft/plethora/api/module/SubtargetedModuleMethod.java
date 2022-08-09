package pw.switchcraft.plethora.api.module;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.ContextKeys;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IPartialContext;
import pw.switchcraft.plethora.api.method.IUnbakedContext;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

/**
 * A top-level module method which requires a particular context object to execute.
 */
public abstract class SubtargetedModuleMethod<T> extends ModuleContainerMethod {
	private final Class<T> klass;

	public SubtargetedModuleMethod(String name, Set<Identifier> modules, Class<T> klass, String docs) {
		this(name, modules, klass, 0, docs);
	}

	public SubtargetedModuleMethod(String name, Set<Identifier> modules, Class<T> klass, int priority, String docs) {
		super(name, modules, priority, docs);
		this.klass = klass;
	}

  @Nonnull
	public static <T> SubtargetedModuleMethod<T> of(String name, Set<Identifier> modules, Class<T> klass, String docs, Delegate<IModuleContainer> delegate, boolean worldThread) {
		return new SubtargetedModuleMethod<T>(name, modules, klass, docs) {
			@Nonnull
			@Override
			public FutureMethodResult apply(@Nonnull IUnbakedContext<IModuleContainer> context, @Nonnull IArguments args) throws LuaException {
				return worldThread
					? FutureMethodResult.nextTick(() -> delegate.apply(context, args))
					: delegate.apply(context, args);
			}
		};
	}

  @Nonnull
	public static <T> SubtargetedModuleMethod<T> of(String name, Identifier module, Class<T> klass, String docs, Delegate<IModuleContainer> delegate, boolean worldThread) {
		return of(name, Collections.singleton(module), klass, docs, delegate, worldThread);
	}

  @Nonnull
	public static <T> SubtargetedModuleMethod<T> of(String name, Set<Identifier> modules, Class<T> klass, String docs, Delegate<IModuleContainer> delegate) {
		return of(name, modules, klass, docs, delegate, true);
	}

  @Nonnull
	public static <T> SubtargetedModuleMethod<T> of(String name, Identifier module, Class<T> klass, String docs, Delegate<IModuleContainer> delegate) {
		return of(name, module, klass, docs, delegate, true);
	}

	@Override
	public boolean canApply(@Nonnull IPartialContext<IModuleContainer> context) {
		if (!super.canApply(context)) return false;
		if (context.hasContext(ContextKeys.ORIGIN, klass)) return true;

		for (Identifier module : getModules()) {
			if (context.hasContext(module.toString(), klass)) return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public Class<T> getSubTarget() {
		return klass;
	}
}
