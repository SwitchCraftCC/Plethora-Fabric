package io.sc3.plethora.core

import com.google.common.collect.MapMaker
import dan200.computercraft.api.lua.LuaException
import io.sc3.plethora.Plethora
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.ICostHandler
import java.util.concurrent.Callable

/**
 * A basic [ICostHandler] implementation. Every object registered with it is updated every tick.
 *
 * @see PlethoraCore.initializeCore
 */
class DefaultCostHandler : ICostHandler {
  private val cfg
    get() = Plethora.config.costSystem

  private var value: Double = cfg.initial
  @Synchronized
  override fun get() = value

  @Synchronized
  override fun consume(amount: Double): Boolean {
    require(amount >= 0) { "amount must be >= 0" }

    if (cfg.allowNegative) {
      if (value <= 0) return false
    } else {
      if (amount > value) return false
    }

    value -= amount
    return true
  }

  @Throws(LuaException::class)
  override fun await(amount: Double, next: FutureMethodResult): FutureMethodResult {
    // First try to consume as normal, unwrapping if not possible.
    if (consume(amount)) return next

    // Otherwise if we'll never be able to consume then give up.
    if (!cfg.allowNegative && amount > cfg.limit || !cfg.awaitRegen) {
      throw LuaException("Insufficient energy (requires $amount, has $value.")
    }

    return FutureMethodResult.awaiting({ consume(amount) }) { next }
  }

  @Throws(LuaException::class)
  override fun await(amount: Double, next: Callable<FutureMethodResult>): FutureMethodResult {
    // First try to consume as normal, unwrapping if not possible.
    if (consume(amount)) {
      return try {
        next.call()
      } catch (e: Exception) {
        when (e) {
          is LuaException -> throw e
          else -> {
            Plethora.log.error("Unexpected error", e)
            throw LuaException("Java Exception Thrown: $e")
          }
        }
      }
    }

    // Otherwise if we'll never be able to consume then give up.
    if (!cfg.allowNegative && amount > cfg.limit || !cfg.awaitRegen) {
      throw LuaException("Insufficient energy (requires $amount, has $value).")
    }

    return FutureMethodResult.awaiting({ consume(amount) }, next)
  }

  @Synchronized
  private fun regen() {
    if (value < cfg.limit) value = cfg.limit.coerceAtMost(value + cfg.regen)
  }

  companion object {
    /**
     * Used to store all handlers.
     *
     * This uses a custom map in order to ensure the keys are compared by identity, rather than equality.
     */
    private val handlers: MutableMap<Any, DefaultCostHandler> = MapMaker()
      .weakKeys().concurrencyLevel(1).makeMap()

    @JvmStatic
    operator fun get(owner: Any): ICostHandler {
      synchronized(handlers) {
        var handler = handlers[owner]
        if (handler == null) {
          handler = DefaultCostHandler()
          handlers[owner] = handler
        }
        return handler
      }
    }

    @JvmStatic
    fun update() {
      synchronized(handlers) {
        for (handler in handlers.values) {
          handler.regen()
        }
      }
    }

    @JvmStatic
    fun reset() {
      synchronized(handlers) { handlers.clear() }
    }
  }
}
