package pw.switchcraft.plethora.api.method;

import dan200.computercraft.api.lua.MethodResult;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class FutureMethodResult {
	private static final FutureMethodResult empty = new FutureMethodResult(MethodResult.of());

	private final MethodResult result;
	private final Callable<FutureMethodResult> next;
	private final Resolver resolver;

	private FutureMethodResult(MethodResult result) {
		this.result = result;
		next = null;
		resolver = IMMEDIATE;
	}

	private FutureMethodResult(Callable<FutureMethodResult> next, Resolver resolver) {
		Objects.requireNonNull(next, "next cannot be null");
		Objects.requireNonNull(resolver, "resolver cannot be null");

		result = null;
		this.next = next;
		this.resolver = resolver;
	}

	public boolean isFinal() {
		return next == null;
	}

	public MethodResult getResult() {
		if (!isFinal()) throw new IllegalStateException("FutureMethodResult is a callback");
		return result;
	}

	public Callable<FutureMethodResult> getCallback() {
		if (isFinal()) throw new IllegalStateException("FutureMethodResult is a result");
		return next;
	}

	public Resolver getResolver() {
		if (isFinal()) throw new IllegalStateException("FutureMethodResult is a result");
		return resolver;
	}

	/**
	 * Defer a function until next tick
	 *
	 * @param next The callback to execute
	 * @return The built MethodResult
	 * @see #nextTick(Runnable)
	 */
	public static FutureMethodResult nextTick(Callable<FutureMethodResult> next) {
		return new FutureMethodResult(next, IMMEDIATE);
	}

	/**
	 * Defer a function until next tick
	 *
	 * @param next The callback to execute
	 * @return The built MethodResult
	 * @see #nextTick(Callable)
	 */
	public static FutureMethodResult nextTick(Runnable next) {
		return new FutureMethodResult(wrap(next), IMMEDIATE);
	}

	/**
	 * Delay a function by a number of ticks
	 *
	 * @param delay The number of ticks to sit idle before executing. 0 will result in the method being executed next tick.
	 * @param next  The callback to execute
	 * @return The built MethodResult
	 */
	public static FutureMethodResult delayed(int delay, Callable<FutureMethodResult> next) {
		return new FutureMethodResult(next, delay <= 0 ? IMMEDIATE : new DelayedResolver(delay));
	}

	/**
	 * Execute a method when the resolver evaluates to true
	 *
	 * @param resolver The resolver to wait on
	 * @param next     The callback to execute
	 * @return THe built MethodResult
	 */
	public static FutureMethodResult awaiting(Resolver resolver, Callable<FutureMethodResult> next) {
		return new FutureMethodResult(next, resolver);
	}

	/**
	 * Get a final method MethodResult
	 *
	 * @param args The arguments to return
	 * @return The built MethodResult
	 */
  @Nonnull
	public static FutureMethodResult result(Object... args) {
		return new FutureMethodResult(MethodResult.of(args));
	}

	/**
	 * Get a final method MethodResult
	 *
	 * @param arg The argument to return
	 * @return The built MethodResult
	 */
  @Nonnull
	public static FutureMethodResult result(Object arg) {
		return new FutureMethodResult(MethodResult.of(arg));
	}

	/**
	 * Get a final MethodResult representing a failure
	 *
	 * @param message The failure message
	 * @return The built MethodResult
	 */
  @Nonnull
	public static FutureMethodResult failure(String message) {
		return new FutureMethodResult(MethodResult.of(false, message));
	}

	/**
	 * Get a final MethodResult with no values
	 *
	 * @return An empty MethodResult
	 */
  @Nonnull
	public static FutureMethodResult empty() {
		return empty;
	}

	private static Callable<FutureMethodResult> wrap(final Runnable runnable) {
		return () -> {
			runnable.run();
			return empty;
		};
	}

	@FunctionalInterface
	public interface Resolver {
		boolean update();
	}

	private static class DelayedResolver implements Resolver {
		private int remaining;

		DelayedResolver(int remaining) {
			this.remaining = remaining;
		}

		@Override
		public boolean update() {
			return remaining-- == 0;
		}
	}

	private static final Resolver IMMEDIATE = () -> true;
}
