package io.sc3.plethora.api.meta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A registry for metadata providers.
 *
 * @see IMetaProvider
 */
public interface IMetaRegistry {
	/**
	 * An list of all valid providers for a class
	 *
	 * @param target The class to get data about
	 * @return List of valid providers
	 */
	@Nonnull
	List<IMetaProvider<?>> getMetaProviders(@Nonnull Class<?> target);

	// TODO: javadoc
	<T extends IMetaProvider<?>> void registerMetaProvider(@Nonnull String name, @Nullable String mod, @Nonnull Class<?> target, @Nonnull T value);
}
