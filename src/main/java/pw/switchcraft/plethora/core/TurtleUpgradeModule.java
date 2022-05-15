package pw.switchcraft.plethora.core;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import pw.switchcraft.plethora.api.IPlayerOwnable;
import pw.switchcraft.plethora.api.IWorldLocation;
import pw.switchcraft.plethora.api.TurtleWorldLocation;
import pw.switchcraft.plethora.api.method.ContextKeys;
import pw.switchcraft.plethora.api.module.IModuleAccess;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.api.module.SingletonModuleContainer;
import pw.switchcraft.plethora.api.reference.ConstantReference;
import pw.switchcraft.plethora.api.reference.IReference;
import pw.switchcraft.plethora.api.reference.Reference;
import pw.switchcraft.plethora.core.executor.TaskRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Wraps a module item as a turtle upgrade.
 */
public class TurtleUpgradeModule implements ITurtleUpgrade {
	private final IModuleHandler handler;
	private final ItemStack stack;
	private final String adjective;

	protected TurtleUpgradeModule(@Nonnull ItemStack stack, @Nonnull IModuleHandler handler, @Nonnull String adjective) {
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
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.PERIPHERAL;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingItem() {
		return stack;
	}

	protected boolean isBlacklisted() {
		return false;
		// TODO: Module blacklist
//		String moduleName = handler.getModule().toString();
//		return ConfigCore.Blacklist.blacklistModulesTurtle.contains(moduleName) || ConfigCore.Blacklist.blacklistModules.contains(moduleName);
	}

	@Override
	public IPeripheral createPeripheral(@Nonnull final ITurtleAccess turtle, @Nonnull final TurtleSide side) {
		if (isBlacklisted()) return null;

		MethodRegistry registry = MethodRegistry.instance;

		final TurtleModuleAccess access = new TurtleModuleAccess(turtle, side, handler);

		final IModuleContainer container = access.getContainer();
		IReference<IModuleContainer> containerRef = new ConstantReference<IModuleContainer>() {
			@Nonnull
			@Override
			public IModuleContainer get() throws LuaException {
				if (turtle.getUpgrade(side) != TurtleUpgradeModule.this) throw new LuaException("The upgrade is gone");
				return container;
			}

			@Nonnull
			@Override
			public IModuleContainer safeGet() throws LuaException {
				return get();
			}
		};

		ContextFactory<IModuleContainer> factory = ContextFactory.of(container, containerRef)
			.withCostHandler(DefaultCostHandler.get(turtle))
			.withModules(container, containerRef)
			.addContext(ContextKeys.ORIGIN, new TurtlePlayerOwnable(turtle))
			.addContext(ContextKeys.ORIGIN, new TurtleWorldLocation(turtle))
			.addContext(ContextKeys.ORIGIN, turtle, Reference.id(turtle));

		handler.getAdditionalContext(stack, access, factory);

		Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> paired = registry.getMethodsPaired(factory.getBaked());
		if (paired.getLeft().isEmpty()) return null;

		AttachableWrapperPeripheral peripheral = new AttachableWrapperPeripheral(handler.getModule().toString(), this, paired, new TaskRunner(), factory.getAttachments());
		access.wrapper = peripheral;
		return peripheral;
	}

	@Nonnull
	@Override
	public TurtleCommandResult useTool(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side, @Nonnull TurtleVerb verb, @Nonnull Direction direction) {
		return TurtleCommandResult.failure("Cannot use tool");
	}

	// TODO: getModel
//	@Nonnull
//	@Override
//	@SideOnly(Side.CLIENT)
//	public Pair<IBakedModel, Matrix4f> getModel(ITurtleAccess turtle, @Nonnull TurtleSide side) {
//		float xOffset = side == TurtleSide.Left ? -0.40625f : 0.40625f;
//		Matrix4f transform = new Matrix4f(
//			0.0f, 0.0f, 1.0f, -0.5f,
//			0.0F, 1.0f, 0.0f, -0.5f,
//			-1.0f, 0.0f, 0.0f, 0.5f,
//			0.0f, 0.0f, 0.0f, 1.0f
//		);
//
//
//		Pair<IBakedModel, Matrix4f> pair = handler.getModel(0);
//		transform.mul(pair.getRight(), transform);
//
//		transform.mul(new Matrix4f(
//			0.8f, 0.0f, 0.0f, 0.5f + xOffset,
//			0.0f, 0.8f, 0.0f, 0.6f,
//			0.0f, 0.0f, 0.8f, 0.475f,
//			0.0f, 0.0f, 0.0f, 1.0f
//		), transform);
//
//		// Translate -0.5 -0.5 -0.5
//		// Rotate Y PI/2
//		// Normal transform
//		// Scale 0.8
//		// Ideally we'd flip if we're on the left side, but that mucks up culling.
//		// Translate 0.5 0.5 0.5
//		// Translate xOffset 0.1 -0.025
//
//		return Pair.of(pair.getLeft(), transform);
//	}

	@Nonnull
	@Override
	public TransformedModel getModel(@org.jetbrains.annotations.Nullable ITurtleAccess turtle, @Nonnull TurtleSide side) {
		return null;
	}

	@Override
	public void update(@Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side) {
		IPeripheral peripheral = turtle.getPeripheral(side);
		if (peripheral instanceof MethodWrapperPeripheral) {
			// TODO
			// ((MethodWrapperPeripheral) peripheral).getRunner().update();
		}
	}

	private static final class TurtleModuleAccess implements IModuleAccess {
		private AttachableWrapperPeripheral wrapper;

		private final ITurtleAccess access;
		private final TurtleSide side;
		private final IWorldLocation location;
		private final IModuleContainer container;

		private TurtleModuleAccess(ITurtleAccess access, TurtleSide side, IModuleHandler handler) {
			this.access = access;
			this.side = side;
			location = new TurtleWorldLocation(access);
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
			return access.getUpgradeNBTData(side);
		}

		@Override
		public void markDataDirty() {
			access.updateUpgradeNBTData(side);
		}

		@Override
		public void queueEvent(@Nonnull String event, @Nullable Object... args) {
			if (wrapper != null) wrapper.queueEvent(event, args);
		}
	}

	public static class TurtlePlayerOwnable implements ConstantReference<TurtlePlayerOwnable>, IPlayerOwnable {
		private final ITurtleAccess access;

		public TurtlePlayerOwnable(ITurtleAccess access) {
			this.access = access;
		}

		@Nullable
		@Override
		public GameProfile getOwningProfile() {
			return access.getOwningPlayer();
		}

		@Nonnull
		@Override
		public TurtlePlayerOwnable get() {
			return this;
		}

		@Nonnull
		@Override
		public TurtlePlayerOwnable safeGet() {
			return this;
		}
	}
}
