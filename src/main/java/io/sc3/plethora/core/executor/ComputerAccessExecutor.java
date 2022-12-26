package io.sc3.plethora.core.executor;

import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.WorkMonitor;
import io.sc3.plethora.api.method.FutureMethodResult;
import io.sc3.plethora.api.method.IResultExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A result executor which attempts to run through {@link IComputerAccess#queueEvent(String, Object[])}
 *
 * This attempts to ensure you're still attached to the computer every tick. If you are not, it'll cancel all tasks
 * and throw an exception.
 *
 * Note that this should not be vulnerable to blocking after a peripheral detaches, as we do not filter our events and
 * so should receive {@code peripheral_detach} events (allowing us to detect the detach and so error).
 */
public class ComputerAccessExecutor implements IResultExecutor {
	private static final String EVENT_NAME = "plethora_task";

	private final IComputerAccess access;
	private final String attachmentName;
	private final TaskRunner runner;

	private volatile boolean attached;

	public ComputerAccessExecutor(IComputerAccess access, TaskRunner runner) {
		this.access = access;
		attachmentName = access.getAttachmentName();
		this.runner = runner;
	}

	@Nullable
	@Override
	public MethodResult execute(@Nonnull FutureMethodResult result, @Nonnull ILuaContext context) throws LuaException {
		assertAttached();
		if (result.isFinal()) return result.getResult();

		long taskId = runner.getNewTaskId();

		ComputerTask task = new ComputerTask(this, result.getCallback(), result.getResolver(), true, taskId);
		boolean ok = runner.submit(task);
		if (!ok) {
			throw new LuaException("Task limit exceeded");
		}

		return new ComputerTaskCallback(taskId, task, this::assertAttached).pull;
	}

	@Override
	public void executeAsync(@Nonnull FutureMethodResult result) throws LuaException {
		assertAttached();
		if (result.isFinal()) return;

		long taskId = runner.getNewTaskId();

		ComputerTask task = new ComputerTask(this, result.getCallback(), result.getResolver(), false, taskId);
		boolean ok = runner.submit(task);
		if (!ok) {
			task.cancel();
			throw new LuaException("Task limit exceeded");
		}
	}

	private void assertAttached() throws LuaException {
		if (!attached) throw new LuaException("Peripheral '" + attachmentName + "' is no longer attached");
	}

	public void attach() {
		attached = true;
	}

	public void detach() {
		attached = false;
	}

	private static final class ComputerTaskCallback implements ILuaCallback {
		private final MethodResult pull = MethodResult.pullEvent(EVENT_NAME, this);
		private final long taskId;
		private final Task originalTask;
		private TaskBody assertAttached;

		private ComputerTaskCallback(long taskId, Task originalTask, TaskBody assertAttached) {
			this.taskId = taskId;
			this.originalTask = originalTask;
			this.assertAttached = assertAttached;
		}

		@Nonnull
		@Override
		public MethodResult resume(Object[] response) throws LuaException {
			if (response.length < 2 || !(response[1] instanceof Number id)) return pull;
			if (id.longValue() != taskId) return pull;

			assertAttached.run();
			if (!originalTask.isDone()) return pull;

			if (originalTask.error != null) throw originalTask.error;
			if (originalTask.result != null) return originalTask.result;
			return MethodResult.of();
		}

		@FunctionalInterface
		private interface TaskBody {
			void run() throws LuaException;
		}
	}

	private static class ComputerTask extends Task {
		private final WorkMonitor monitor;
		private final ComputerAccessExecutor executor;
		private final boolean shouldQueue;
		private final long taskId;

		ComputerTask(ComputerAccessExecutor executor, Callable<FutureMethodResult> callback,
					 FutureMethodResult.Resolver resolver, boolean shouldQueue, long taskId) {
			super(callback, resolver);
			this.executor = executor;
			this.shouldQueue = shouldQueue;
			this.taskId = taskId;
			monitor = executor.access.getMainThreadMonitor();
		}

		@Override
		void whenDone() {
			super.whenDone();
			if (!executor.attached || !shouldQueue) return;

			try {
				executor.access.queueEvent(EVENT_NAME, taskId);
			} catch (RuntimeException ignored) {
				// There is sadly nothing we can do about this, as there's always a slight
				// chance of a race condition.
			}
		}

		@Override
		boolean canWork() {
			return monitor == null || monitor.shouldWork();
		}

		@Override
		protected void submitTiming(long time) {
			super.submitTiming(time);
			if (monitor != null) monitor.trackWork(time, TimeUnit.NANOSECONDS);
		}

		@Override
		public boolean update() {
			if (!executor.attached) {
				cancel();
				return true;
			}

			return super.update();
		}
	}
}
