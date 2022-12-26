package io.sc3.plethora.api.method;

import net.minecraft.util.math.Direction;
import io.sc3.plethora.api.PlethoraAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Various helper methods for costs
 */
public final class CostHelpers {
	private CostHelpers() {
		throw new IllegalStateException("Cannot instantiate singleton " + getClass().getName());
	}

	/**
	 * Get the cost handler for this object
	 *
	 * @param object The cost handler's owner
	 * @return The associated cost handler
	 */
	@Nonnull
	public static ICostHandler getCostHandler(@Nonnull Object object) {
		return PlethoraAPI.instance().methodRegistry().getCostHandler(object, null);
	}

	/**
	 * Get the cost handler for this object
	 *
	 * @param object The cost handler's owner
	 * @param side   The side to get the cost handler from.
	 * @return The associated cost handler
	 */
	@Nonnull
	public static ICostHandler getCostHandler(@Nonnull Object object, @Nullable Direction side) {
		return PlethoraAPI.instance().methodRegistry().getCostHandler(object, side);
	}
}
