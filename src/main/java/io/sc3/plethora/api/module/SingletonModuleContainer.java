package io.sc3.plethora.api.module;

import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * A module container which only contains one item.
 */
public class SingletonModuleContainer implements IModuleContainer {
	private final Identifier thisModule;
	private final Set<Identifier> modules;

	public SingletonModuleContainer(@Nonnull Identifier module) {
		Objects.requireNonNull(module, "module cannot be null");
		thisModule = module;
		modules = Collections.singleton(module);
	}

	@Override
	public boolean hasModule(@Nonnull Identifier module) {
		return thisModule.equals(module);
	}

	@Nonnull
	@Override
	public Set<Identifier> getModules() {
		return modules;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SingletonModuleContainer that = (SingletonModuleContainer) o;

		return thisModule.equals(that.thisModule);
	}

	@Override
	public int hashCode() {
		return thisModule.hashCode();
	}
}
