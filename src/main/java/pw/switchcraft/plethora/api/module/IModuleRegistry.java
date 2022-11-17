package pw.switchcraft.plethora.api.module;

import pw.switchcraft.plethora.api.vehicle.IVehicleUpgradeHandler;

import javax.annotation.Nonnull;

/**
 * Various helper methods for modules.
 */
public interface IModuleRegistry {
	/**
	 * Convert a module handler to a vehicle upgrade handler.
	 *
	 * @param handler The module handler to convert.
	 * @return The resulting vehicle upgrade handler.
	 */
	IVehicleUpgradeHandler toVehicleUpgrade(@Nonnull IModuleHandler handler);
}
