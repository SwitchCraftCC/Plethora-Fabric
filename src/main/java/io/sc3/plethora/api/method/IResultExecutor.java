package io.sc3.plethora.api.method;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTask;
import dan200.computercraft.api.lua.MethodResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An object which evaluates a {@link MethodResult}.
 * <p>
 * By default, this uses {@link ILuaContext#executeMainThreadTask(LuaTask)} to execute the task,
 * but some instances will execute on the TileEntity's tick instead
 */
public interface IResultExecutor {
	/**
	 * Execute a task and wait for the result.
	 * <p>
	 * This should immediately return {@link MethodResult#getResult()} if the result is final,
	 * otherwise defer for the specified delay and wait til that task has finished.
	 *
	 * @param result  The method result to evaluate
	 * @param context The context to evaluate under
	 * @return The final result
	 * @throws LuaException If something went wrong
	 */
	@Nullable
	MethodResult execute(@Nonnull FutureMethodResult result, @Nonnull ILuaContext context) throws LuaException;

	/**
	 * Execute a task, without waiting for the result to execute.
	 *
	 * @param result The method result to evaluate
	 * @throws LuaException If something went wrong when queueing the task
	 */
	void executeAsync(@Nonnull FutureMethodResult result) throws LuaException;
}
