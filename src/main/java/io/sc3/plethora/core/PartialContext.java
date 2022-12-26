package io.sc3.plethora.core;

import io.sc3.plethora.api.meta.TypedMeta;
import io.sc3.plethora.api.method.ContextKeys;
import io.sc3.plethora.api.method.ICostHandler;
import io.sc3.plethora.api.method.IPartialContext;
import io.sc3.plethora.api.module.IModuleContainer;

import javax.annotation.Nonnull;
import java.util.*;

public class PartialContext<T> implements IPartialContext<T> {
	protected final int target;
	protected final String[] keys;
	protected final Object[] values;
	protected final ICostHandler handler;
	protected final IModuleContainer modules;

	public PartialContext(int target, String[] keys, @Nonnull Object[] values, @Nonnull ICostHandler handler, @Nonnull IModuleContainer modules) {
		this.target = target;
		this.keys = keys;
		this.handler = handler;
		this.values = values;
		this.modules = modules;
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public T getTarget() {
		return (T) values[target];
	}

	PartialContext<?> withIndex(int index) {
		return index == target ? this : new PartialContext(index, keys, values, handler, modules);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V getContext(@Nonnull Class<V> klass) {
		Objects.requireNonNull(klass, "klass cannot be null");

		for (int i = values.length - 1; i >= 0; i--) {
			Object obj = values[i];
			if (klass.isInstance(obj)) return (V) obj;
		}

		return null;
	}

	@Override
	public <V> boolean hasContext(@Nonnull Class<V> klass) {
		Objects.requireNonNull(klass, "klass cannot be null");

		for (int i = values.length - 1; i >= 0; i--) {
			Object obj = values[i];
			if (klass.isInstance(obj)) return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V getContext(@Nonnull String contextKey, @Nonnull Class<V> klass) {
		Objects.requireNonNull(contextKey, "contextKey cannot be null");
		Objects.requireNonNull(klass, "klass cannot be null");

		for (int i = values.length - 1; i >= 0; i--) {
			Object obj = values[i];
			if (contextKey.equals(keys[i]) && klass.isInstance(obj)) return (V) obj;
		}

		return null;
	}

	@Override
	public <V> boolean hasContext(@Nonnull String contextKey, @Nonnull Class<V> klass) {
		Objects.requireNonNull(klass, "klass cannot be null");

		for (int i = values.length - 1; i >= 0; i--) {
			Object obj = values[i];
			if (contextKey.equals(keys[i]) && klass.isInstance(obj)) return true;
		}

		return false;
	}

	@Nonnull
	@Override
	public <U> PartialContext<U> makePartialChild(@Nonnull U target) {
		Objects.requireNonNull(target, "target cannot be null");

		ArrayList<String> keys = new ArrayList<>(this.keys.length + 1);
		ArrayList<Object> values = new ArrayList<>(this.values.length + 1);

		Collections.addAll(keys, this.keys);
		Collections.addAll(values, this.values);

		for (int i = keys.size() - 1; i >= 0; i--) {
			if (!ContextKeys.TARGET.equals(keys.get(i))) continue;
			keys.set(i, ContextKeys.GENERIC);
		}

		// Add the new target and convert it.
		keys.add(ContextKeys.TARGET);
		values.add(target);
		ConverterRegistry.instance.extendConverted(keys, values, this.values.length);

		return new PartialContext<>(this.values.length, keys.toArray(new String[0]), values.toArray(), handler, modules);
	}

	@Nonnull
	@Override
	public ICostHandler getCostHandler() {
		return handler;
	}

	@Nonnull
	@Override
	public IModuleContainer getModules() {
		return modules;
	}

	@Nonnull
	@Override
	public TypedMeta<T, ?> getMeta() {
		return MetaRegistry.instance.getMeta(this);
	}
}
