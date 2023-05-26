package io.sc3.plethora.integration

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IMethodCollection
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.BasicModuleContainer
import io.sc3.plethora.api.module.IModuleContainer
import net.minecraft.util.Identifier

object CoreMethods {
  val LIST_MODULES = BasicMethod.of(
    "listModules", "function():table -- Lists all modules available"
  ) { unbaked, _ -> listModules(unbaked) }
  private fun listModules(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val container = unbaked.bake().target
    val modules = container.modules
      .mapIndexed { i, module -> i + 1 to module.toString() }
      .toMap()
    return FutureMethodResult.result(modules)
  }

  val HAS_MODULE = BasicMethod.of(
    "hasModule", "function(module:string):boolean -- Checks whether a module is available",
    ::hasModule
  )
  private fun hasModule(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val container = unbaked.bake().target
    val module = args.getString(0)
    return FutureMethodResult.result(container.hasModule(Identifier(module)))
  }

  val FILTER_MODULES = BasicMethod.of(
    "filterModules", "function(names:string...):table|nil -- Gets the methods which require these modules",
    ::filterModules
  )
  private fun filterModules(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult? {
    val context = unbaked.bake()
    val oldModules = context.target.modules
    val newModules = mutableSetOf<Identifier>()

    for (i in 0 until args.count()) {
      val module = Identifier(args.getString(i))
      if (oldModules.contains(module)) newModules.add(module)
    }

    if (newModules.isEmpty()) {
      return null
    }

    val obj = context
      .makeChildId(BasicModuleContainer(newModules))
      .getObject()

    return FutureMethodResult.result(if (obj.methodNames.isEmpty()) null else obj)
  }

  val GET_DOCS = BasicMethod.of(
    "getDocs", "function([name: string]):string|table -- Get the documentation for all functions or the function " +
      "specified. Errors if the function cannot be found.",
    ::getDocs
  )
  private fun getDocs(unbaked: IUnbakedContext<IMethodCollection>, args: IArguments): FutureMethodResult {
    val methodCollection = unbaked.bake().target
    val name = args.optString(0, null)

    return if (name == null) {
      FutureMethodResult.result(methodCollection.methods()
        .associate { it.name to it.docString })
    } else {
      FutureMethodResult.result(methodCollection.methods()
        .firstOrNull { it.name == name }
        ?.docString
        ?: throw LuaException("No such method"))
    }
  }
}
