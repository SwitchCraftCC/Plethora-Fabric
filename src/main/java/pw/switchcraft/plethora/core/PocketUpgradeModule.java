package pw.switchcraft.plethora.core;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pw.switchcraft.plethora.api.EntityWorldLocation;
import pw.switchcraft.plethora.api.IPlayerOwnable;
import pw.switchcraft.plethora.api.IWorldLocation;
import pw.switchcraft.plethora.api.method.ContextKeys;
import pw.switchcraft.plethora.api.method.IAttachable;
import pw.switchcraft.plethora.api.module.IModuleAccess;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.api.module.SingletonModuleContainer;
import pw.switchcraft.plethora.api.reference.ConstantReference;
import pw.switchcraft.plethora.api.reference.IReference;
import pw.switchcraft.plethora.core.executor.TaskRunner;
import pw.switchcraft.plethora.util.PlayerHelpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Wraps a module item as a pocket upgrade.
 */
class PocketUpgradeModule implements IPocketUpgrade {
	private final IModuleHandler handler;
	private final ItemStack stack;
	private final String adjective;

	PocketUpgradeModule(@Nonnull ItemStack stack, @Nonnull IModuleHandler handler, @Nonnull String adjective) {
		this.handler = handler;
		this.stack = stack;
		this.adjective = adjective;
	}

	@Nonnull
	@Override
	public Identifier getUpgradeID() {
		return handler.getModule();
	}

	@Nonnull
	@Override
	public String getUnlocalisedAdjective() {
		return adjective;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingItem() {
		return stack;
	}

	@Override
	public IPeripheral createPeripheral(@Nonnull final IPocketAccess pocket) {
		final Identifier thisModule = handler.getModule();

		String moduleName = thisModule.toString();
		// TODO: Check module blacklist
//		if (ConfigCore.Blacklist.blacklistModulesPocket.contains(moduleName) || ConfigCore.Blacklist.blacklistModules.contains(moduleName)) {
//			return null;
//		}

		MethodRegistry registry = MethodRegistry.instance;

		final Entity entity = pocket.getEntity();

		final PocketModuleAccess access = new PocketModuleAccess(pocket, handler);
		final IModuleContainer container = access.getContainer();
		IReference<IModuleContainer> containerRef = new ConstantReference<IModuleContainer>() {
			@Nonnull
			@Override
			public IModuleContainer get() throws LuaException {
				if (!pocket.getUpgrades().containsKey(getUpgradeID())) {
					throw new LuaException("The upgrade is gone");
				}
				return container;
			}

			@Nonnull
			@Override
			public IModuleContainer safeGet() throws LuaException {
				return get();
			}
		};

		IWorldLocation location = new LastEntityLocation(pocket);

		ContextFactory<IModuleContainer> factory = ContextFactory.of(container, containerRef)
			.withCostHandler(DefaultCostHandler.get(pocket))
			.withModules(container, containerRef)
			.addContext(ContextKeys.ORIGIN, new PocketPlayerOwnable(access))
			.addContext(ContextKeys.ORIGIN, location)
			.addContext(ContextKeys.ORIGIN, entity, new ConstantReference<Entity>() {
				@Nonnull
				@Override
				public Entity get() throws LuaException {
					Entity accessEntity = pocket.getEntity();

					// TODO: Just do a null check?
					if (accessEntity != entity) throw new LuaException("Entity has changed");
					return accessEntity;
				}

				@Nonnull
				@Override
				public Entity safeGet() throws LuaException {
					return get();
				}
			});

		handler.getAdditionalContext(stack, access, factory);

		Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> paired = registry.getMethodsPaired(factory.getBaked());
		return paired.getLeft().isEmpty() ? null : new PocketPeripheral(this, access, paired, factory.getAttachments());
	}

	@Override
	public void update(@Nonnull IPocketAccess access, IPeripheral peripheral) {
		if (peripheral instanceof PocketPeripheral methodWrapper) {
			// Invalidate peripheral
			if (methodWrapper.getEntity() != access.getEntity()) {
				access.invalidatePeripheral();
			}

			// Update the task runner
			methodWrapper.getRunner().update();
		}
	}

	@Override
	public boolean onRightClick(@Nonnull World world, @Nonnull IPocketAccess access, IPeripheral peripheral) {
		return false;
	}

	private static final class PocketPeripheral extends AttachableWrapperPeripheral {
		private final Entity entity;

		public PocketPeripheral(
			PocketUpgradeModule owner, PocketModuleAccess access,
			Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> methods,
			List<IAttachable> attachments
		) {
			super(owner.getUpgradeID().toString(), owner, methods, new TaskRunner(), attachments);
			entity = access.entity;
			access.wrapper = this;
		}

		public Entity getEntity() {
			return entity;
		}

		@Override
		public boolean equals(IPeripheral other) {
			return super.equals(other) && other instanceof PocketPeripheral && entity == ((PocketPeripheral) other).entity;
		}
	}

	private static final class PocketModuleAccess implements IModuleAccess {
		private AttachableWrapperPeripheral wrapper;

		private final IPocketAccess access;
		private final Entity entity;
		private final IWorldLocation location;
		private final IModuleContainer container;

		private PocketModuleAccess(IPocketAccess access, IModuleHandler handler) {
			entity = access.getEntity();
			location = new EntityWorldLocation(entity);
			this.access = access;
			container = new SingletonModuleContainer(handler.getModule());
		}

		@Nonnull
		@Override
		public Entity getOwner() {
			return entity;
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
			return access.getUpgradeNBTData();
		}

		@Nonnull
		@Override
		public MinecraftServer getServer() {
			return Objects.requireNonNull(location.getWorld().getServer()); // TODO
		}

		@Override
		public void markDataDirty() {
			access.updateUpgradeNBTData();
		}

		@Override
		public void queueEvent(@Nonnull String event, @Nullable Object... args) {
			if (wrapper != null) wrapper.queueEvent(event, args);
		}
	}

	public static class PocketPlayerOwnable implements ConstantReference<PocketPlayerOwnable>, IPlayerOwnable {
		private final PocketModuleAccess access;

		public PocketPlayerOwnable(PocketModuleAccess access) {
			this.access = access;
		}

		@Nullable
		@Override
		public GameProfile getOwningProfile() {
			return PlayerHelpers.getProfile(access.getOwner());
		}

		@Nonnull
		@Override
		public PocketPlayerOwnable get() {
			return this;
		}

		@Nonnull
		@Override
		public PocketPlayerOwnable safeGet() {
			return this;
		}
	}

	private static class LastEntityLocation implements IWorldLocation {
		private final IPocketAccess pocket;
		private Entity lastEntity;

		LastEntityLocation(IPocketAccess pocket) {
			this.pocket = pocket;
			lastEntity = pocket.getEntity();
		}

		@Nonnull
		@Override
		public World getWorld() {
			return lastEntity.getEntityWorld();
		}

		@Nonnull
		@Override
		public BlockPos getPos() {
			return lastEntity.getBlockPos();
		}

		@Nonnull
		@Override
		public Vec3d getLoc() {
			return lastEntity.getPos();
		}

		@Nonnull
		@Override
		public Box getBounds() {
			return lastEntity.getBoundingBox(); // TODO: Can collision bounding box be used here? Is it even needed?
		}

		@Nonnull
		@Override
		public IWorldLocation get() throws LuaException {
			Entity entity = pocket.getEntity();
			if (entity == null) throw new LuaException("Entity is not there");
			lastEntity = entity;
			return this;
		}

		@Nonnull
		@Override
		public IWorldLocation safeGet() {
			return this;
		}

		@Override
		public boolean isConstant() {
			return true;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
				.append("pocket", pocket)
				.append("lastEntity", lastEntity)
				.append("world", getWorld())
				.append("loc", getLoc())
				.toString();
		}
	}
}
