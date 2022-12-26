package io.sc3.plethora.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import io.sc3.plethora.Plethora;
import io.sc3.plethora.api.converter.IConverterExcludeMethod;
import io.sc3.plethora.api.method.*;
import io.sc3.plethora.core.collections.ClassIteratorIterable;
import io.sc3.plethora.util.config.ConfigLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static io.sc3.plethora.Plethora.log;

public final class MethodRegistry implements IMethodRegistry {
	public static final MethodRegistry instance = new MethodRegistry();

	final List<RegisteredMethod<?>> all = new ArrayList<>();
	final Multimap<Class<?>, RegisteredMethod<?>> providers = MultimapBuilder.hashKeys().arrayListValues().build();

	private void registerMethod(@Nonnull RegisteredMethod<?> entry) {
		Objects.requireNonNull(entry, "entry cannot be null");

		if (entry.getTarget() == Object.class && entry.getMethod().has(IConverterExcludeMethod.class)) {
			log.warn(
				"You're registering a method (" + entry.getRegName() + ") targeting the base class (Object). Converters will " +
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

		log.debug("Registering method {} for {}", mod + ":" + name, target);
		registerMethod(new RegisteredMethod.Impl<>(name, mod, target, method));
	}

	public void build() {
		log.debug("Building method registry");
		providers.clear();
		for (RegisteredMethod<?> entry : all) {
			if (entry.enabled()) {
				entry.build();
				providers.put(entry.getTarget(), entry);
			}
		}

    // At this stage, RegisteredMethod.build() should have called computeIfAbsent for every method's base cost.
    // Re-save the configuration to ensure that the base cost is saved.
    log.info("Updating base costs in configuration file");
    ConfigLoader.INSTANCE.saveConfig(Plethora.config);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> List<RegisteredMethod<T>> getMethods(@Nonnull IPartialContext<T> context) {
		Objects.requireNonNull(context, "context cannot be null");

		List<RegisteredMethod<T>> methods = Lists.newArrayList();

		// TODO: Would be nice to be able to do all of this with no reflection at all.
		for (Class<?> klass : new ClassIteratorIterable(context.getTarget().getClass())) {
			for (RegisteredMethod entry : providers.get(klass)) {
				if (entry.getMethod().canApply(context)) methods.add(entry);
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
				IMethod<?> method = entry.getMethod();
				// Skip IConverterExclude methods
				if (i != builder.target && method.has(IConverterExcludeMethod.class)) continue;

				if (unbaked == null) unbaked = builder.unbake().withIndex(i);

				Integer existing = methodLookup.get(method.getName());
				if (existing != null) {
					int index = existing;
					if (method.getPriority() > methods.get(index).getMethod().getPriority()) {
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
				IMethod<?> method = entry.getMethod();
				Integer existing = methodLookup.get(method.getName());
				if (existing != null) {
					int index = existing;
					if (method.getPriority() > methods.get(index).getMethod().getPriority()) {
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
