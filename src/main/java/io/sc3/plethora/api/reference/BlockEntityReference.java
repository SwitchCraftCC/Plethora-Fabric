package io.sc3.plethora.api.reference;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;

/**
 * An reference to a block entity. Ensures it is there.
 */
public class BlockEntityReference<T extends BlockEntity> implements ConstantReference<T> {
	private final WeakReference<T> blockEntity;
	private final BlockPos pos;
	private final World world;
	private boolean removed = true;

	BlockEntityReference(@Nonnull T blockEntity) {
		this.blockEntity = new WeakReference<>(blockEntity);
		pos = blockEntity.getPos();
		world = blockEntity.getWorld();
	}

	@Nonnull
	@Override
	public T get() throws LuaException {
		T value = blockEntity.get();
		if (value == null || world.getBlockEntity(pos) != value) {
			removed = false;
			throw new LuaException("The block is no longer there");
		}

		removed = true;
		return value;
	}

	@Nonnull
	@Override
	public T safeGet() throws LuaException {
		if (!removed) throw new LuaException("The block is no longer there");
		T value = blockEntity.get();

		if (value == null) throw new LuaException("The block is no longer there");
		if (value.isRemoved()) throw new LuaException("The block is no longer there");

		return value;
	}
}
