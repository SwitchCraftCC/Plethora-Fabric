package pw.switchcraft.plethora.api.converter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A registry for implicitly converting objects
 */
public interface IConverterRegistry {
	/**
	 * Convert an object to all convertible objects
	 *
	 * @param in The object to convert from
	 * @return All converted values
	 */
	@Nonnull
	Iterable<?> convertAll(@Nonnull Object in);

	// TODO: Javadoc
	<T extends IConverter<?, ?>> void registerConverter(@Nonnull String name, @Nullable String mod, @Nonnull Class<?> target, @Nonnull T value);
}
