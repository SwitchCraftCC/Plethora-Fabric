package pw.switchcraft.plethora.api.vehicle;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.module.BasicModuleHandler;

/**
 * A {@link BasicModuleHandler} which also provides a {@link IVehicleUpgradeHandler}.
 */
public class VehicleModuleHandler extends BasicModuleHandler {
	private IVehicleUpgradeHandler handler;

	public VehicleModuleHandler(Identifier id, Item item) {
		super(id, item);
	}

	protected IVehicleUpgradeHandler createVehicle() {
		return null; // TODO
		// return PlethoraAPI.instance().moduleRegistry().toVehicleUpgrade(this);
	}
}
