package io.sc3.plethora.core;

import io.sc3.plethora.api.module.IModuleHandler;
import io.sc3.plethora.api.module.IModuleRegistry;
import io.sc3.plethora.api.vehicle.IVehicleUpgradeHandler;

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
