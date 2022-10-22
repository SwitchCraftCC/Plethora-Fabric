package pw.switchcraft.plethora.core;

import com.google.common.collect.MapMaker;
import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.ICostHandler;
import pw.switchcraft.plethora.util.config.Config.CostSystem;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A basic {@link ICostHandler} implementation. Every object registered with it is updated every tick.
 *
 * @see PlethoraCore#initializeCore()
 */
public final class DefaultCostHandler implements ICostHandler {
	/**
	 * Used to store all handlers.
	 *
	 * This uses a custom map in order to ensure the keys are compared by identity, rather than equality.
	 */
	private static final Map<Object, DefaultCostHandler> handlers = new MapMaker()
		.weakKeys().concurrencyLevel(1).makeMap();

	private double value = CostSystem.initial;

	@Override
	public synchronized double get() {
		return value;
	}

	@Override
	public synchronized boolean consume(double amount) {
		if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");

		if (CostSystem.allowNegative) {
			if (value <= 0) return false;
		} else {
			if (amount > value) return false;
		}

		value -= amount;
		return true;
	}

	@Override
	public FutureMethodResult await(double amount, FutureMethodResult next) throws LuaException {
		// First try to consume as normal, unwrapping if not possible.
		if (consume(amount)) return next;

		// Otherwise if we'll never be able to consume then give up.
		if ((!CostSystem.allowNegative && amount > CostSystem.limit) || !CostSystem.awaitRegen) {
			throw new LuaException("Insufficient energy (requires " + amount + ", has " + value + ".");
		}

		return FutureMethodResult.awaiting(() -> consume(amount), () -> next);
	}

	@Override
	public FutureMethodResult await(double amount, Callable<FutureMethodResult> next) throws LuaException {
		// First try to consume as normal, unwrapping if not possible.
		if (consume(amount)) {
			try {
				return next.call();
			} catch (LuaException e) {
				throw e;
			} catch (Exception | LinkageError | VirtualMachineError e) {
				Plethora.LOG.error("Unexpected error", e);
				throw new LuaException("Java Exception Thrown: " + e);
			}
		}

		// Otherwise if we'll never be able to consume then give up.
		if ((!CostSystem.allowNegative && amount > CostSystem.limit) || !CostSystem.awaitRegen) {
			throw new LuaException("Insufficient energy (requires " + amount + ", has " + value + ").");
		}

		return FutureMethodResult.awaiting(() -> consume(amount), next);
	}

	private synchronized void regen() {
		if (value < CostSystem.limit) value = Math.min(CostSystem.limit, value + CostSystem.regen);
	}

	public static ICostHandler get(Object owner) {
		synchronized (handlers) {
			DefaultCostHandler handler = handlers.get(owner);
			if (handler == null) {
				handler = new DefaultCostHandler();
				handlers.put(owner, handler);
			}

			return handler;
		}
	}

	public static void update() {
		synchronized (handlers) {
			for (DefaultCostHandler handler : handlers.values()) {
				handler.regen();
			}
		}
	}

	public static void reset() {
		synchronized (handlers) {
			handlers.clear();
		}
	}
}
