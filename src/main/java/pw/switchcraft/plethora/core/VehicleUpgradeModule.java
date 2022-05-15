package pw.switchcraft.plethora.core;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import pw.switchcraft.plethora.api.EntityWorldLocation;
import pw.switchcraft.plethora.api.IWorldLocation;
import pw.switchcraft.plethora.api.method.ContextKeys;
import pw.switchcraft.plethora.api.method.CostHelpers;
import pw.switchcraft.plethora.api.method.ICostHandler;
import pw.switchcraft.plethora.api.module.IModuleAccess;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.api.module.SingletonModuleContainer;
import pw.switchcraft.plethora.api.reference.ConstantReference;
import pw.switchcraft.plethora.api.reference.IReference;
import pw.switchcraft.plethora.api.reference.Reference;
import pw.switchcraft.plethora.api.vehicle.IVehicleAccess;
import pw.switchcraft.plethora.api.vehicle.IVehicleUpgradeHandler;
import pw.switchcraft.plethora.core.executor.TaskRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VehicleUpgradeModule implements IVehicleUpgradeHandler {
	private final IModuleHandler handler;

	public VehicleUpgradeModule(IModuleHandler handler) {
		this.handler = handler;
	}

	// TODO: getModel
//	@Nonnull
//	@Override
//	@SideOnly(Side.CLIENT)
//	public Pair<IBakedModel, Matrix4f> getModel(@Nonnull IVehicleAccess access) {
//		Pair<IBakedModel, Matrix4f> model = handler.getModel(0);
//
//		Matrix4f transform = new Matrix4f();
//		transform.setIdentity();
//
//		// Center the view (-0.5) and then move half a pixel back out.
//		transform.setTranslation(new Vector3f(0, 0, -0.5f + (1 / 32.0f)));
//
//		transform.mul(transform, model.getRight());
//		return Pair.of(model.getLeft(), transform);
//	}

	@Override
	public void update(@Nonnull IVehicleAccess vehicle, @Nonnull IPeripheral peripheral) {
		if (peripheral instanceof MethodWrapperPeripheral) {
			// TODO
			// ((MethodWrapperPeripheral) peripheral).getRunner().update();
		}
	}

	@Nullable
	@Override
	public IPeripheral create(@Nonnull IVehicleAccess vehicle) {
		final Identifier thisModule = handler.getModule();

		String moduleName = thisModule.toString();
		// TODO: Module blacklist
//		if (ConfigCore.Blacklist.blacklistModulesVehicle.contains(moduleName) || ConfigCore.Blacklist.blacklistModules.contains(moduleName)) {
//			return null;
//		}

		MethodRegistry registry = MethodRegistry.instance;
		Entity entity = vehicle.getVehicle();

		ICostHandler cost = CostHelpers.getCostHandler(entity, null);

		final VehicleModuleAccess access = new VehicleModuleAccess(vehicle, handler);

		final IModuleContainer container = access.getContainer();
		IReference<IModuleContainer> containerRef = new ConstantReference<IModuleContainer>() {
			@Nonnull
			@Override
			public IModuleContainer get() {
				// if (turtle.getUpgrade(side) != TurtleUpgradeModule.this) throw new LuaException("The upgrade is gone");
				// TODO: Correctly invalidate this peripheral when it is detached.
				return container;
			}

			@Nonnull
			@Override
			public IModuleContainer safeGet() {
				return get();
			}
		};

		ContextFactory<IModuleContainer> factory = ContextFactory.of(container, containerRef)
			.withCostHandler(cost)
			.withModules(container, containerRef)
			.addContext(ContextKeys.ORIGIN, new EntityWorldLocation(entity))
			.addContext(ContextKeys.ORIGIN, vehicle, Reference.id(vehicle))
			.addContext(ContextKeys.ORIGIN, vehicle.getVehicle(), Reference.entity(vehicle.getVehicle()));

		handler.getAdditionalContext(ItemStack.EMPTY, access, factory); // TODO: ItemStack

		Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> paired = registry.getMethodsPaired(factory.getBaked());
		if (paired.getLeft().isEmpty()) return null;

		AttachableWrapperPeripheral peripheral = new AttachableWrapperPeripheral(moduleName, this, paired, new TaskRunner(), factory.getAttachments());
		access.wrapper = peripheral;
		return peripheral;
	}

	private static final class VehicleModuleAccess implements IModuleAccess {
		private AttachableWrapperPeripheral wrapper;

		private final IVehicleAccess access;
		private final IWorldLocation location;
		private final IModuleContainer container;

		private VehicleModuleAccess(IVehicleAccess access, IModuleHandler handler) {
			this.access = access;
			location = new EntityWorldLocation(access.getVehicle());
			container = new SingletonModuleContainer(handler.getModule());
		}

		@Nonnull
		@Override
		public Object getOwner() {
			return access;
		}

		@Nonnull
		@Override
		public IWorldLocation getLocation() {
			return location;
		}

		@Nonnull
		@Override
		public IModuleContainer getContainer() {
			return container;
		}

		@Nonnull
		@Override
		public NbtCompound getData() {
			return access.getData();
		}

		@Override
		public void markDataDirty() {
			access.markDataDirty();
		}

		@Override
		public void queueEvent(@Nonnull String event, @Nullable Object... args) {
			if (wrapper != null) wrapper.queueEvent(event, args);
		}
	}
}
