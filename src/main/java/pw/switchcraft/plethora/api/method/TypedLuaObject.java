package pw.switchcraft.plethora.api.method;

import dan200.computercraft.api.lua.IDynamicLuaObject;

/**
 * A {@link IDynamicLuaObject} which targets a specific type.
 *
 * @param <T> The type this object targets
 * @see IContext#getObject()
 */
public interface TypedLuaObject<T> extends IDynamicLuaObject {
}
