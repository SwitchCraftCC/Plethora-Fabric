package pw.switchcraft.plethora.core

import dan200.computercraft.api.lua.LuaException
import pw.switchcraft.plethora.api.method.FutureMethodResult
import pw.switchcraft.plethora.api.method.ICostHandler
import java.util.concurrent.Callable

object EmptyCostHandler : ICostHandler {
  override fun get() = 0.0

  override fun consume(amount: Double): Boolean {
    require(amount >= 0) { "amount must be >= 0" }
    return amount == 0.0
  }

  @Throws(LuaException::class)
  override fun await(amount: Double, next: Callable<FutureMethodResult>): FutureMethodResult =
    throw LuaException("Insufficient energy (requires $amount, has 0).")
}
