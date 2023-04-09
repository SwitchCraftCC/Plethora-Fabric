package io.sc3.plethora.api.reference;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import io.sc3.plethora.util.EntityHelpers;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * A reference to an entity. Ensures it is still alive.
 */
public class EntityReference<T extends Entity> implements ConstantReference<T> {
	private final MinecraftServer server;
	private final UUID id;
	private WeakReference<T> entity;

	EntityReference(T entity) {
		server = entity.getServer();
		id = entity.getUuid();
		this.entity = new WeakReference<>(entity);
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	@Override
	public T get() throws LuaException {
		T entity = this.entity.get();

		if (entity == null || !entity.isAlive() || entity.getEntityWorld().getEntityById(entity.getId()) != entity) {
			entity = (T) EntityHelpers.getEntityFromUuid(server, id);
			if (entity == null || !entity.isAlive()) throw new LuaException("The entity is no longer there");

			this.entity = new WeakReference<>(entity);
		}

		return entity;
	}

	@Nonnull
	@Override
	public T safeGet() throws LuaException {
		T value = entity.get();
		if (value == null || !value.isAlive()) throw new LuaException("The entity is no longer there");

		return value;
	}
}
