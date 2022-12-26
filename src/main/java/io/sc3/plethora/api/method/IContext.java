package io.sc3.plethora.api.method;

import dan200.computercraft.api.lua.IDynamicLuaObject;
import io.sc3.plethora.api.reference.IReference;
import io.sc3.plethora.api.reference.Reference;

import javax.annotation.Nonnull;

/**
 * This holds the context for a method.
 *
 * This tracks the current object and all parent/associated objects
 */
public interface IContext<T> extends IPartialContext<T> {
    /**
     * Make a child context
     *
     * @param target          The child's target
     * @param targetReference A reference to child's target
     * @return The child context
     */
    @Nonnull
    <U> IContext<U> makeChild(U target, @Nonnull IReference<U> targetReference);

    /**
     * Make a child context
     *
     * @param target The child's target
     * @return The child context
     */
    @Nonnull
    <U extends IReference<U>> IContext<U> makeChild(@Nonnull U target);

    /**
     * Make a child context, using {@link Reference#id(Object)}
     *
     * @param target The child's target
     * @return The child context
     */
    @Nonnull
    <U> IContext<U> makeChildId(@Nonnull U target);

    /**
     * Get the unbaked context for this context.
     *
     * @return The unbaked context.
     */
    @Nonnull
    IUnbakedContext<T> unbake();

    /**
     * Find all methods which may be applied to this context, and bundle them into a
     * {@link IDynamicLuaObject}.
     *
     * @return The converted object
     */
    @Nonnull
    TypedLuaObject<T> getObject();
}
