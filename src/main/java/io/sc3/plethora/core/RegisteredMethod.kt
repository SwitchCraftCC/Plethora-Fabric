package io.sc3.plethora.core

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import io.sc3.plethora.Plethora
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IMethod
import io.sc3.plethora.api.method.IUnbakedContext

abstract class RegisteredMethod<T>(
  regName: String,
  mod: String,
  val target: Class<T>
) : RegisteredValue(regName, mod) {
  private val cfg by Plethora.config::costSystem
  private var cost = 0

  abstract val method: IMethod<T>

  fun build() {
    // val comment = method.name + ": " + method.docString
    val name = "$mod:$regName"
    cost = cfg.baseCosts.computeIfAbsent(name) { 0 }

    if (cost < 0) {
      Plethora.log.warn("Cost for method $name is negative! Setting to 0.")
      cost = 0
    }
  }

  @Throws(LuaException::class)
  fun call(context: IUnbakedContext<T>, args: IArguments): FutureMethodResult = try {
    if (cost <= 0) {
      method.apply(context, args)
    } else {
      // This is a little suboptimal, as argument validation will be deferred until later.
      // However, we don't have much of a way round this as the method could technically
      // have side effects.
      context.costHandler.await(cost.toDouble()) { method.apply(context, args) }
    }
  } catch (e: Exception) {
    when (e) {
      is LuaException -> throw e
      else -> {
        Plethora.log.error("Unexpected error calling $regName", e)
        throw LuaException("Java Exception Thrown: $e")
      }
    }
  }

  class Impl<T>(name: String, mod: String, target: Class<T>,
                override val method: IMethod<T>) : RegisteredMethod<T>(name, mod, target)
}
