package io.sc3.plethora.integration.vanilla.registry

import io.sc3.plethora.api.method.IMethod
import io.sc3.plethora.api.method.IMethodRegistry
import io.sc3.plethora.integration.vanilla.method.EntityIntrospectionMethods
import io.sc3.plethora.integration.vanilla.method.EntityKineticMethods
import io.sc3.plethora.integration.vanilla.method.RangedInventoryWrapperMethods
import net.minecraft.util.Identifier

object VanillaMethodRegistration {
  @JvmStatic
  fun registerMethods(registry: IMethodRegistry) {
    with (registry) {
      method("introspection:getInventory", EntityIntrospectionMethods.GET_INVENTORY)
      method("introspection:getEquipment", EntityIntrospectionMethods.GET_EQUIPMENT)
      method("introspection:getEnder", EntityIntrospectionMethods.GET_ENDER_CHEST)
      method("kinetic:look", EntityKineticMethods.LOOK)
      method("kinetic:use", EntityKineticMethods.USE)
      method("kinetic:swing", EntityKineticMethods.SWING)

      method("introspection:consume", RangedInventoryWrapperMethods.CONSUME)
      method("introspection:drop", RangedInventoryWrapperMethods.DROP)
    }
  }

  private inline fun <reified T> IMethodRegistry.method(name: String, method: IMethod<T>) {
    registerMethod(Identifier.DEFAULT_NAMESPACE, name, T::class.java, method)
  }
}
