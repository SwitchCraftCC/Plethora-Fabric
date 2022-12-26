package pw.switchcraft.plethora.core;

import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.api.module.IModuleRegistry;
import pw.switchcraft.plethora.api.vehicle.IVehicleUpgradeHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

final class ModuleRegistry implements IModuleRegistry {
	public static final ModuleRegistry instance = new ModuleRegistry();

	private ModuleRegistry() {
	}

	@Override
	public IVehicleUpgradeHandler toVehicleUpgrade(@Nonnull IModuleHandler handler) {
		Objects.requireNonNull(handler, "handler cannot be null");

		return new VehicleUpgradeModule(handler);
	}
}
