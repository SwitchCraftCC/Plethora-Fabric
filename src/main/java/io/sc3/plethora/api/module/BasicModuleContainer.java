package io.sc3.plethora.api.module;

import net.minecraft.util.Identifier;
import io.sc3.plethora.api.reference.IReference;
import io.sc3.plethora.api.reference.Reference;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * A basic implementation of a module container.
 */
public class BasicModuleContainer implements IModuleContainer {
	public static final IModuleContainer EMPTY = new BasicModuleContainer(Collections.emptySet());
	public static final IReference<IModuleContainer> EMPTY_REF = Reference.id(EMPTY);

	private final Set<Identifier> modules;

	public BasicModuleContainer(@Nonnull Set<Identifier> modules) {
		Objects.requireNonNull(modules, "modules cannot be null");
		this.modules = Collections.unmodifiableSet(modules);
	}

	@Override
	public boolean hasModule(@Nonnull Identifier module) {
		return modules.contains(module);
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

		BasicModuleContainer that = (BasicModuleContainer) o;

		return modules.equals(that.modules);
	}

	@Override
	public int hashCode() {
		return modules.hashCode();
	}
}
