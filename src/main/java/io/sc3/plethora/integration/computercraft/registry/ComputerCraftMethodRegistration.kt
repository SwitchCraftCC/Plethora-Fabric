package io.sc3.plethora.integration.computercraft.registry

import dan200.computercraft.api.ComputerCraftAPI
import io.sc3.plethora.api.method.IMethod
import io.sc3.plethora.api.method.IMethodRegistry
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.integration.computercraft.InventoryMethodsWrapper
import io.sc3.plethora.integration.computercraft.method.TurtleKineticMethods
import net.minecraft.inventory.Inventory

object ComputerCraftMethodRegistration {
  @JvmStatic
  fun registerMethods(r: IMethodRegistry) {
    with (r) {
      // Modules
      moduleMethod("kinetic:use", TurtleKineticMethods.USE)
      moduleMethod("kinetic:swing", TurtleKineticMethods.SWING)

      // Inventory wrapper
      inventoryMethod("getSize", InventoryMethodsWrapper.GET_SIZE)
      inventoryMethod("list", InventoryMethodsWrapper.LIST)
      inventoryMethod("getItemDetail", InventoryMethodsWrapper.GET_ITEM_DETAIL)
      inventoryMethod("getItemLimit", InventoryMethodsWrapper.GET_ITEM_LIMIT)
      inventoryMethod("pushItems", InventoryMethodsWrapper.PUSH_ITEMS)
      inventoryMethod("pullItems", InventoryMethodsWrapper.PULL_ITEMS)
    }
  }

  private fun <T> IMethodRegistry.method(name: String, target: Class<T>, method: IMethod<T>) {
    registerMethod(ComputerCraftAPI.MOD_ID, name, target, method)
  }

  private fun IMethodRegistry.moduleMethod(name: String, method: IMethod<IModuleContainer>) {
    method(name, IModuleContainer::class.java, method)
  }

  private fun IMethodRegistry.inventoryMethod(name: String, method: IMethod<Inventory>) {
    method("inventory:$name", Inventory::class.java, method)
  }
}
