package pw.switchcraft.plethora.api.method;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A registry for metadata providers.
 *
 * @see IMethod
 */
public interface IMethodRegistry {
	/**
	 * Get the cost handler for this object
	 *
	 * @param object The cost handler's owner
	 * @param side   The side to get the cost handler from
	 * @return The associated cost handler
	 */
	@Nonnull
	ICostHandler getCostHandler(@Nonnull Object object, @Nullable Direction side);

	<T> void registerMethod(@Nonnull Identifier identifier, @Nonnull Class<T> target, IMethod<T> method);
}
