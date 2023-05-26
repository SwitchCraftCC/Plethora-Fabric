package io.sc3.plethora.integration

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.PlethoraAPI
import io.sc3.plethora.api.converter.IConverterExcludeMethod
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IPartialContext
import io.sc3.plethora.api.method.IUnbakedContext
import java.util.concurrent.Callable

class GetMetadataMethod : BasicMethod<Any>(
  "getMetadata",
  Int.MIN_VALUE,
  "function():table -- Get metadata about this object"
), IConverterExcludeMethod {
  override fun canApply(context: IPartialContext<Any>): Boolean {
    val registry = PlethoraAPI.instance().metaRegistry()
    val target = context.target

    if (registry.getMetaProviders(target.javaClass).isNotEmpty()) {
      return true
    }

    // Convert all and check if any matches
    for (converted in PlethoraAPI.instance().converterRegistry().convertAll(target)) {
      if (registry.getMetaProviders(converted!!.javaClass).isNotEmpty()) {
        return true
      }
    }

    return false
  }

  override fun apply(context: IUnbakedContext<Any>, args: IArguments): FutureMethodResult =
    FutureMethodResult.nextTick(Callable { FutureMethodResult.result(context.bake().meta) })
}
