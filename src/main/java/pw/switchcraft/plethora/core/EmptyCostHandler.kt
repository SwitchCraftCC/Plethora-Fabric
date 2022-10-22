package pw.switchcraft.plethora.core;

import dan200.computercraft.api.lua.LuaException;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.ICostHandler;

import java.util.concurrent.Callable;

public class EmptyCostHandler implements ICostHandler {
	public static final ICostHandler INSTANCE = new EmptyCostHandler();

	private EmptyCostHandler() {
	}

	@Override
	public double get() {
		return 0;
	}

	@Override
	public boolean consume(double amount) {
		if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
		return amount == 0;
	}

	@Override
	public FutureMethodResult await(double amount, Callable<FutureMethodResult> next) throws LuaException {
		throw new LuaException("Insufficient energy (requires " + amount + ", has 0).");
	}
}
