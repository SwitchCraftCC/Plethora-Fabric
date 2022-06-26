package pw.switchcraft.plethora.core;

import dan200.computercraft.api.lua.LuaException;
import joptsimple.internal.Strings;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.ContextKeys;
import pw.switchcraft.plethora.api.method.IMethod;
import pw.switchcraft.plethora.api.method.IPartialContext;
import pw.switchcraft.plethora.api.method.IUnbakedContext;

import java.util.Arrays;

/** Replacement helpers for the old @FromTarget, @FromSubtarget, and @FromContext annotations. */
public final class ContextHelpers {
    /**
     * Extract this value from an unbaked context's target.
     *
     * This method is thread safe, though the result object may not be safe to use on any thread.
     *
     * @see #fromTarget(IPartialContext)
     *
     * @param <T> The type of the value to extract.
     * @param ctx The unbaked context to extract the value from.
     */
    public static <T> T safeFromTarget(IUnbakedContext<T> ctx) throws LuaException {
        return ctx.safeBake().getTarget();
    }

    /**
     * Extract this value from the context's target.
     *
     * This is just equivalent to {@link #fromContext(IPartialContext, Class, String[])} with a context key of
     * {@link ContextKeys#TARGET}.
     *
     * @param <T> The type of the value to extract.
     * @param ctx The context to extract the value from.
     */
    public static <T> T fromTarget(IPartialContext<T> ctx) {
        // TODO: Check this returns the correct type
        return ctx.getTarget();
    }

    /**
     * Extract this value from the context's target.
     *
     * This is largely similar to {@link #fromContext(IPartialContext, Class, String[])}, though is more selective in
     * where it extracts the entry, and the type will be used as a {@link IMethod#getSubTarget()}.
     *
     * @param <U> The type of the value to extract.
     * @param ctx The context to extract the value from.
     * @param cls The class of the value to extract.
     * @param keys The context key to extract from. When blank, this will be equivalent to {@link ContextKeys#ORIGIN}
     *             and then all modules you may be attached to.
     */
    public static <T, U> U fromSubtarget(IPartialContext<T> ctx, Class<U> cls, String... keys) {
        if (keys.length == 0) {
            // No keys given, start with the origin and then check all modules. Return the first matching value.
            U val = ctx.getContext(ContextKeys.ORIGIN, cls);
            if (val != null) return val;

            for (Identifier name : ctx.getModules().getModules()) {
                val = ctx.getContext(name, cls);
                if (val != null) return val;
            }
        } else {
            // Check only the keys we were given
            U val;
            for (String key : keys) {
                val = ctx.getContext(key, cls);
                if (val != null) return val;
            }
        }

        return null; // TODO: Is this okay?
    }

    /**
     * Extract this value from the context's target, searching {@link ContextKeys#ORIGIN} and then all attached modules.
     *
     * This is largely similar to {@link #fromContext(IPartialContext, Class, String[])}, though is more selective in
     * where it extracts the entry, and the type will be used as a {@link IMethod#getSubTarget()}.
     *
     * @param <U> The type of the value to extract.
     * @param ctx The context to extract the value from.
     * @param cls The class of the value to extract.
     */
    public static <T, U> U fromSubtarget(IPartialContext<T> ctx, Class<U> cls) {
        return fromSubtarget(ctx, cls, new String[0]);
    }

    /**
     * Extract this value from the context.
     *
     * @param <U> The type of the value to extract.
     * @param ctx The context to extract the value from.
     * @param cls The class of the value to extract.
     * @param keys The context key to extract from. When blank, the whole context will be searched.
     */
    public static <T, U> U fromContext(IPartialContext<T> ctx, Class<U> cls, String... keys) {
        if (keys.length == 0 || (keys.length == 1 && Strings.isNullOrEmpty(keys[0]))) {
            return ctx.getContext(cls);
        } else {
            // Check only the keys we were given
            U val;
            for (String key : keys) {
                val = ctx.getContext(key, cls);
                if (val != null) return val;
            }
        }

        return null; // TODO: Is this okay?
    }

    /**
     * Extract this value from the context.
     *
     * @param <U> The type of the value to extract.
     * @param ctx The context to extract the value from.
     * @param cls The class of the value to extract.
     * @param keys The context key to extract from. When blank, the whole context will be searched.
     */
    public static <T, U> U fromContext(IPartialContext<T> ctx, Class<U> cls, Identifier... keys) {
        return fromContext(ctx, cls, Arrays.stream(keys).map(Identifier::toString).toArray(String[]::new));
    }

    /**
     * Extract this value from the context. The whole context will be searched.
     *
     * @param <U> The type of the value to extract.
     * @param ctx The context to extract the value from.
     * @param cls The class of the value to extract.
     */
    public static <T, U> U fromContext(IPartialContext<T> ctx, Class<U> cls) {
        return fromContext(ctx, cls, new String[0]);
    }
}
