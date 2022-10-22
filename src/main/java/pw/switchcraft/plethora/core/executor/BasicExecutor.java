package pw.switchcraft.plethora.core.executor;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaTask;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IResultExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

import static pw.switchcraft.plethora.Plethora.log;

/**
 * A result executor which relies on {@link ILuaContext#executeMainThreadTask(ILuaTask)} in order to execute tasks.
 *
 * This queues a task every tick until all results in the chain have been resolved and evaluated. This could end up
 * being rather spamming in terms of events, but does not usually become an issue as most tasks finish within the tick.
 */
public final class BasicExecutor implements IResultExecutor {
	public static final BasicExecutor INSTANCE = new BasicExecutor();

	private BasicExecutor() {
	}

	@Override
	@Nullable
	public MethodResult execute(@Nonnull FutureMethodResult result, @Nonnull ILuaContext context) throws LuaException {
		if (result.isFinal()) return result.getResult();

		BlockingTask task = new BlockingTask(result.getResolver(), result.getCallback());
		return context.executeMainThreadTask(task);

//		while (!task.done()) {
//			context.executeMainThreadTask(task);
//		}
//		return MethodResult.of(task.returnValue);
	}

	@Override
	public void executeAsync(@Nonnull FutureMethodResult result) throws LuaException {
		if (result.isFinal()) return;

		Task task = new Task(result.getCallback(), result.getResolver());
		boolean ok = TaskRunner.SHARED.submit(task);
		if (!ok) {
			task.cancel();
			throw new LuaException("Task limit exceeded");
		}
	}

	private static class BlockingTask implements ILuaTask {
		Object[] returnValue;
		private FutureMethodResult.Resolver resolver;
		private Callable<FutureMethodResult> callback;

		BlockingTask(FutureMethodResult.Resolver resolver, Callable<FutureMethodResult> callback) {
			this.resolver = resolver;
			this.callback = callback;
		}

		@Override
		public Object[] execute() throws LuaException {
			while (resolver.update()) {
				resolver = null;

				try {
					FutureMethodResult result = callback.call();
					if (result.isFinal()) {
						returnValue = result.getResult().getResult();
						return new Object[] { returnValue };
					} else {
						resolver = result.getResolver();
						callback = result.getCallback();
					}
				} catch (LuaException e) {
					throw e;
				} catch (Exception | LinkageError | VirtualMachineError e) {
					log.error("Unexpected error", e);
					throw new LuaException("Java Exception Thrown: " + e);
				}
			}

			return null;
		}

		boolean done() {
			return resolver == null;
		}
	}
}
