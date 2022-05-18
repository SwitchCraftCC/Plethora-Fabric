package pw.switchcraft.plethora.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.api.converter.IConverterExcludeMethod;
import pw.switchcraft.plethora.api.method.*;
import pw.switchcraft.plethora.core.collections.ClassIteratorIterable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class MethodRegistry implements IMethodRegistry {
	public static final MethodRegistry instance = new MethodRegistry();

	final List<RegisteredMethod<?>> all = new ArrayList<>();
	final Multimap<Class<?>, RegisteredMethod<?>> providers = MultimapBuilder.hashKeys().arrayListValues().build();

	private void registerMethod(@Nonnull RegisteredMethod<?> entry) {
		Objects.requireNonNull(entry, "entry cannot be null");

		if (entry.target() == Object.class && entry.method().has(IConverterExcludeMethod.class)) {
			Plethora.LOG.warn(
				"You're registering a method (" + entry.name() + ") targeting the base class (Object). Converters will " +
					"probably mask the original object: it is recommended that you implement IConverterExcludeMethod to avoid this."
			);
		}

		all.add(entry);
	}

	@Override
	public <T> void registerMethod(@Nonnull String mod, @Nonnull String name, @Nonnull Class<T> target, @Nonnull IMethod<T> method) {
		Objects.requireNonNull(mod, "mod cannot be null");
		Objects.requireNonNull(name, "name cannot be null");
		Objects.requireNonNull(target, "target cannot be null");
		Objects.requireNonNull(method, "method cannot be null");

		Plethora.LOG.debug("Registering method {} for {}", mod + ":" + name, target);
		registerMethod(new RegisteredMethod.Impl<>(mod, name, target, method));
	}

	public void build() {
		Plethora.LOG.debug("Building method registry");
		providers.clear();
		for (RegisteredMethod<?> entry : all) {
			if (entry.enabled()) {
				entry.build();
				providers.put(entry.target(), entry);
			}
		}
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> List<RegisteredMethod<T>> getMethods(@Nonnull IPartialContext<T> context) {
		Objects.requireNonNull(context, "context cannot be null");

		List<RegisteredMethod<T>> methods = Lists.newArrayList();

		// TODO: Would be nice to be able to do all of this with no reflection at all.
		for (Class<?> klass : new ClassIteratorIterable(context.getTarget().getClass())) {
			for (RegisteredMethod entry : providers.get(klass)) {
				if (entry.method().canApply(context)) methods.add(entry);
			}
		}

		return Collections.unmodifiableList(methods);
	}

	@Nonnull
	@Override
	public ICostHandler getCostHandler(@Nonnull Object object, @Nullable Direction side) {
		Objects.requireNonNull(object, "object cannot be null");
		return DefaultCostHandler.get(object);
	}

	public Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> getMethodsPaired(Context<?> builder) {
		ArrayList<RegisteredMethod<?>> methods = Lists.newArrayList();
		ArrayList<UnbakedContext<?>> contexts = Lists.newArrayList();
		HashMap<String, Integer> methodLookup = new HashMap<>();

		String[] keys = builder.keys;
		Object[] values = builder.values;

		for (int i = values.length - 1; i >= 0; i--) {
			if (!ContextKeys.TARGET.equals(keys[i])) continue;

			UnbakedContext<?> unbaked = null;
			for (RegisteredMethod<?> entry : getMethods(builder.withIndex(i))) {
				IMethod<?> method = entry.method();
				// Skip IConverterExclude methods
				if (i != builder.target && method.has(IConverterExcludeMethod.class)) continue;

				if (unbaked == null) unbaked = builder.unbake().withIndex(i);

				Integer existing = methodLookup.get(method.getName());
				if (existing != null) {
					int index = existing;
					if (method.getPriority() > methods.get(index).method().getPriority()) {
						methods.set(index, entry);
						contexts.set(index, unbaked);
					}
				} else {
					methods.add(entry);
					contexts.add(unbaked);
					methodLookup.put(method.getName(), methods.size() - 1);
				}
			}
		}

		if (!methods.isEmpty()) {
			IMethodCollection collection = new MethodCollection(methods);

			Context<IMethodCollection> baked = builder.makeChildId(collection);
			for (RegisteredMethod<?> entry : getMethods(baked)) {
				IMethod<?> method = entry.method();
				Integer existing = methodLookup.get(method.getName());
				if (existing != null) {
					int index = existing;
					if (method.getPriority() > methods.get(index).method().getPriority()) {
						methods.set(index, entry);
						contexts.set(index, baked.unbake());
					}
				} else {
					methods.add(entry);
					contexts.add(baked.unbake());
					methodLookup.put(method.getName(), methods.size() - 1);
				}
			}
		}

		return new Pair<>(methods, contexts);
	}
}
