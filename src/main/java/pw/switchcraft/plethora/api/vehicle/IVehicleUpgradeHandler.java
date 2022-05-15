package pw.switchcraft.plethora.api.vehicle;

import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A capability which provides an upgrade to various vehicles.
 */
public interface IVehicleUpgradeHandler {
	// TODO: getModel

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
