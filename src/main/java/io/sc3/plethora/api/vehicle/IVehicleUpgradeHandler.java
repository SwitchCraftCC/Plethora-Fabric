package io.sc3.plethora.api.vehicle;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A capability which provides an upgrade to various vehicles.
 */
public interface IVehicleUpgradeHandler {
	/**
	 * Get a model from this stack
	 *
	 * @param access The vehicle access
	 * @return A baked model and its transformation
	 * @see TransformedModel
	 */
	@Nonnull
	@Environment(EnvType.CLIENT)
	TransformedModel getModel(@Nonnull IVehicleAccess access);

	/**
	 * Update the vehicle handler for the specific
	 */
	void update(@Nonnull IVehicleAccess vehicle, @Nonnull IPeripheral peripheral);

	/**
	 * Create a peripheral from the given vehicle
	 *
	 * @return The peripheral to create, or {@code null} if none should be created.
	 */
	@Nullable
	IPeripheral create(@Nonnull IVehicleAccess vehicle);
}
