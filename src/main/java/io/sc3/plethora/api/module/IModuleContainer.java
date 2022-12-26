package io.sc3.plethora.api.module;

import net.minecraft.util.Identifier;
import io.sc3.plethora.api.method.IMethod;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An object representing a root level container for modules. This should be used
 * as a target for {@link IMethod} when module methods
 * do not target a specific object.
 */
public interface IModuleContainer {
	/**
	 * Check whether this module container has a given module.
	 *
	 * This will not change over the course of the container's lifetime.
	 *
	 * @param module The module to check
	 * @return Whether this container has a the specified module
	 */
	boolean hasModule(@Nonnull Identifier module);

	/**
	 * Get a collection of all modules in this container..
	 *
	 * This will not change over the course of the containers's lifetime.
	 *
	 * @return A read-only set of all available modules.
	 */
	@Nonnull
	Set<Identifier> getModules();
}
