package io.sc3.plethora.integration.vanilla.registry

import io.sc3.plethora.api.method.IMethod
import io.sc3.plethora.api.method.IMethodRegistry
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.integration.vanilla.method.EntityIntrospectionMethods
import io.sc3.plethora.integration.vanilla.method.EntityKineticMethods
import io.sc3.plethora.integration.vanilla.method.RangedInventoryWrapperMethods
import net.minecraft.util.Identifier

object VanillaMethodRegistration {
  @JvmStatic
  fun registerMethods(registry: IMethodRegistry) {
    with (registry) {
      moduleMethod("introspection:getInventory", EntityIntrospectionMethods.GET_INVENTORY)
      moduleMethod("introspection:getEquipment", EntityIntrospectionMethods.GET_EQUIPMENT)
      moduleMethod("introspection:getEnder", EntityIntrospectionMethods.GET_ENDER_CHEST)
      moduleMethod("kinetic:look", EntityKineticMethods.LOOK)
      moduleMethod("kinetic:use", EntityKineticMethods.USE)
      moduleMethod("kinetic:swing", EntityKineticMethods.SWING)

      method("introspection:consume", RangedInventoryWrapperMethods.CONSUME)
      method("introspection:drop", RangedInventoryWrapperMethods.DROP)
    }
  }

  private inline fun <reified T> IMethodRegistry.method(name: String, method: IMethod<T>) {
    registerMethod(Identifier.DEFAULT_NAMESPACE, name, T::class.java, method)
  }

  private inline fun <reified T> IMethodRegistry.method(method: IMethod<T>) {
    registerMethod(Identifier.DEFAULT_NAMESPACE, method.name, T::class.java, method)
  }

  private fun IMethodRegistry.moduleMethod(name: String, method: IMethod<IModuleContainer>) {
    method(name, method)
  }
}
