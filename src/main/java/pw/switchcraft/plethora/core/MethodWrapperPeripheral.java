package pw.switchcraft.plethora.core;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.ObjectUtils;
import pw.switchcraft.plethora.api.method.ContextKeys;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IResultExecutor;
import pw.switchcraft.plethora.core.executor.ComputerAccessExecutor;
import pw.switchcraft.plethora.core.executor.TaskRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pw.switchcraft.plethora.Plethora.log;

/**
 * Handles integration with a {@link IPeripheral}
 */
public class MethodWrapperPeripheral extends MethodWrapper implements IDynamicPeripheral {
    private final Object owner;
    private final String type;

    private final TaskRunner runner;
    private final Map<IComputerAccess, ComputerAccessExecutor> accesses = new ConcurrentHashMap<>();

    public MethodWrapperPeripheral(
        String name, Object owner, List<RegisteredMethod<?>> methods, List<UnbakedContext<?>> contexts,
        TaskRunner runner
    ) {
        super(methods, contexts);
        this.owner = owner;
        type = name;
        this.runner = runner;
    }

    public MethodWrapperPeripheral(
        String name, Object owner, Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> methods,
        TaskRunner runner
    ) {
        this(name, owner, methods.getLeft(), methods.getRight(), runner);
    }

    @Nonnull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public MethodResult callMethod(
        @Nonnull IComputerAccess access, @Nonnull ILuaContext luaContext, int method, @Nonnull final IArguments args
    ) throws LuaException {
        IResultExecutor executor = accesses.get(access);
        if (executor == null) throw new LuaException("Not attached to this computer");

        UnbakedContext<?> context = getContext(method);
        Object[] extraRef = getReferences(access, luaContext);

        int totalSize = context.keys.length + extraRef.length;
        String[] keys = new String[totalSize];
        System.arraycopy(context.keys, 0, keys, 0, context.keys.length);
        Object[] references = new Object[totalSize];
        System.arraycopy(context.references, 0, references, 0, context.references.length);

        for (int i = 0; i < extraRef.length; i++) {
            keys[context.keys.length + i] = ContextKeys.COMPUTER;
            references[context.keys.length + i] = extraRef[i];
        }

        UnbakedContext<?> full = new UnbakedContext<>(
            context.target, keys, references, context.handler, context.modules, executor
        );

        @SuppressWarnings("unchecked")
        FutureMethodResult result = getMethod(method).call((UnbakedContext) full, args);
        MethodResult executorResult = executor.execute(result, luaContext);
        return ObjectUtils.firstNonNull(executorResult, MethodResult.of());

//        RegisteredMethod<?> registeredMethod = getMethod(method);
//        return luaContext.executeMainThreadTask(() -> registeredMethod.call((UnbakedContext) full, args)
//            .getResult());
    }

    @Override
    public void attach(@Nonnull IComputerAccess access) {
        ComputerAccessExecutor executor = new ComputerAccessExecutor(access, runner);
        executor.attach();

        ComputerAccessExecutor previous = accesses.put(access, executor);
        if (previous != null) previous.detach();// Should never happen but...
    }

    @Override
    public void detach(@Nonnull IComputerAccess access) {
        ComputerAccessExecutor executor = accesses.remove(access);
        if (executor != null) executor.detach();
    }

    public void queueEvent(@Nonnull String name, @Nullable Object... args) {
        for (IComputerAccess access : accesses.keySet()) {
            try {
                access.queueEvent(name, args);
            } catch (RuntimeException e) {
                log.error("Cannot queue event on " + access, e);
            }
        }
    }

    @Nonnull
    public TaskRunner getRunner() {
        return runner;
    }

    /**
     * Technically overrides, but only on CC:Tweaked
     *
     * @return The current object's owner
     */
    @Override
    @Nonnull
    public Object getTarget() {
        return owner;
    }

    @Override
    public boolean equals(IPeripheral other) {
        if (this == other) return true;
        if (!(other instanceof MethodWrapperPeripheral otherP)) return false;
        if (!getType().equals(other.getType())) return false;

        return owner == otherP.owner && equalMethods(otherP);
    }
}
