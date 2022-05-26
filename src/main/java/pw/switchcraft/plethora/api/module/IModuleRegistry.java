package pw.switchcraft.plethora.api.module;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.vehicle.IVehicleUpgradeHandler;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Various helper methods for modules.
 */
public interface IModuleRegistry {
	/**
	 * Register a turtle upgrade.
	 *
	 * This will use the stack's module handler and the item name + ".adjective" for its adjective.
	 *
	 * @param stack The stack containing the module.
	 */
	void registerTurtleUpgrade(@Nonnull ItemStack stack);

	/**
	 * Register a turtle upgrade.
	 *
	 * This will use the stack's module handler.
	 *
	 * @param stack     The stack containing the module.
	 * @param adjective The module's adjective.
	 */
	void registerTurtleUpgrade(@Nonnull ItemStack stack, @Nonnull String adjective);

	/**
	 * Register a turtle upgrade.
	 *
	 * @param stack     The stack containing the module.
	 * @param handler   The module handler.
	 * @param adjective The module's adjective.
	 */
	void registerTurtleUpgrade(@Nonnull ItemStack stack, @Nonnull IModuleHandler handler, @Nonnull String adjective);

	/**
	 * Register a pocket upgrade.
	 *
	 * This will use the stack's module handler and the item name + ".adjective" for its adjective.
	 *
	 * @param stack The stack containing the module.
	 */
	void registerPocketUpgrade(@Nonnull ItemStack stack);

	/**
	 * Register a pocket upgrade.
	 *
	 * This will use the stack's module handler.
	 *
	 * @param stack     The stack containing the module.
	 * @param adjective The module's adjective.
	 */
	void registerPocketUpgrade(@Nonnull ItemStack stack, @Nonnull String adjective);

	/**
	 * Register a pocket upgrade.
	 *
	 * @param stack     The stack containing the module.
	 * @param handler   The module handler.
	 * @param adjective The module's adjective.
	 */
	void registerPocketUpgrade(@Nonnull ItemStack stack, @Nonnull IModuleHandler handler, @Nonnull String adjective);

	/**
	 * Convert a module handler to a vehicle upgrade handler.
	 *
	 * @param handler The module handler to convert.
	 * @return The resulting vehicle upgrade handler.
	 */
	IVehicleUpgradeHandler toVehicleUpgrade(@Nonnull IModuleHandler handler);

	List<IPocketUpgrade> getPocketUpgrades();

	List<ITurtleUpgrade> getTurtleUpgrades();
}
