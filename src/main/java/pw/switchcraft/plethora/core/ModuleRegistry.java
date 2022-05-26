package pw.switchcraft.plethora.core;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import net.minecraft.item.ItemStack;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.api.module.IModuleRegistry;
import pw.switchcraft.plethora.api.vehicle.IVehicleUpgradeHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ModuleRegistry implements IModuleRegistry {
	public static final ModuleRegistry instance = new ModuleRegistry();

	private final List<IPocketUpgrade> pocketUpgrades = new ArrayList<>();
	private final List<ITurtleUpgrade> turtleUpgrades = new ArrayList<>();

	private ModuleRegistry() {
	}

	@Override
	public void registerTurtleUpgrade(@Nonnull ItemStack stack) {
		Objects.requireNonNull(stack, "stack cannot be null");
		registerTurtleUpgrade(stack, stack.getTranslationKey() + ".adjective");
	}

	@Override
	public void registerTurtleUpgrade(@Nonnull ItemStack stack, @Nonnull String adjective) {
		Objects.requireNonNull(stack, "stack cannot be null");
		Objects.requireNonNull(adjective, "adjective cannot be null");

//		IModuleHandler handler = stack.getCapability(Constants.MODULE_HANDLER_CAPABILITY, null);
//		if (handler == null) throw new NullPointerException("stack has no handler");

		if (stack.getItem() instanceof IModuleHandler handler) {
			registerTurtleUpgrade(stack, handler, adjective);
		} else {
			throw new NullPointerException("stack's item is not an IModuleHandler");
		}
	}

	@Override
	public void registerTurtleUpgrade(@Nonnull ItemStack stack, @Nonnull IModuleHandler handler, @Nonnull String adjective) {
		Objects.requireNonNull(stack, "stack cannot be null");
		Objects.requireNonNull(stack, "handler cannot be null");
		Objects.requireNonNull(adjective, "adjective cannot be null");

		ITurtleUpgrade upgrade = new TurtleUpgradeModule(stack, handler, adjective);
		ComputerCraftAPI.registerTurtleUpgrade(upgrade);
		turtleUpgrades.add(upgrade);
	}

	@Override
	public void registerPocketUpgrade(@Nonnull ItemStack stack) {
		Objects.requireNonNull(stack, "stack cannot be null");
		registerPocketUpgrade(stack, stack.getTranslationKey() + ".adjective");
	}

	@Override
	public void registerPocketUpgrade(@Nonnull ItemStack stack, @Nonnull String adjective) {
		Objects.requireNonNull(stack, "stack cannot be null");
		Objects.requireNonNull(adjective, "adjective cannot be null");

//		IModuleHandler handler = stack.getCapability(Constants.MODULE_HANDLER_CAPABILITY, null);
//		if (handler == null) throw new NullPointerException("stack has no handler");

		if (stack.getItem() instanceof IModuleHandler handler) {
			registerPocketUpgrade(stack, handler, adjective);
		} else {
			throw new NullPointerException("stack's item is not an IModuleHandler");
		}
	}

	@Override
	public void registerPocketUpgrade(@Nonnull ItemStack stack, @Nonnull IModuleHandler handler, @Nonnull String adjective) {
		Objects.requireNonNull(stack, "stack cannot be null");
		Objects.requireNonNull(stack, "handler cannot be null");
		Objects.requireNonNull(adjective, "adjective cannot be null");

		IPocketUpgrade upgrade = new PocketUpgradeModule(stack, handler, adjective);
		ComputerCraftAPI.registerPocketUpgrade(upgrade);
		pocketUpgrades.add(upgrade);
	}

	@Override
	public IVehicleUpgradeHandler toVehicleUpgrade(@Nonnull IModuleHandler handler) {
		Objects.requireNonNull(handler, "handler cannot be null");

		return new VehicleUpgradeModule(handler);
	}

	@Override
	public List<IPocketUpgrade> getPocketUpgrades() {
		return pocketUpgrades;
	}

	@Override
	public List<ITurtleUpgrade> getTurtleUpgrades() {
		return turtleUpgrades;
	}
}
