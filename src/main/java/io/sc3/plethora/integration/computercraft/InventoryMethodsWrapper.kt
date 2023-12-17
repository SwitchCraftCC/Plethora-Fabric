package io.sc3.plethora.integration.computercraft

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.shared.peripheral.generic.methods.InventoryMethods
import io.sc3.plethora.api.method.BasicMethod
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.FutureMethodResult.result
import io.sc3.plethora.api.method.IContext
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.core.ContextHelpers
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.inventory.Inventory

object InventoryMethodsWrapper {
  private val inventory = InventoryMethods()

  val GET_SIZE = BasicMethod.of("size", "function():number -- Get the size of this inventory.", ::size)
  private fun size(unbaked: IUnbakedContext<Inventory>, args: IArguments): FutureMethodResult {
    val inv = unbaked.getInventory()
    return result(inv.size())
  }

  val LIST = BasicMethod.of(
    "list", "function():table -- List all items in this inventory. This returns a table, with an entry for each slot.",
    ::list
  )
  private fun list(unbaked: IUnbakedContext<Inventory>, args: IArguments): FutureMethodResult {
    val inv = unbaked.getInventoryStorage()
    return result(inventory.list(inv))
  }

  val GET_ITEM_DETAIL = BasicMethod.of(
    "getItemDetail", "function(slot:number):table -- Get detailed information about an item.", ::getItemDetail
  )
  private fun getItemDetail(unbaked: IUnbakedContext<Inventory>, args: IArguments): FutureMethodResult {
    val slot = args.getInt(0)
    val inv = unbaked.getInventoryStorage()
    return result(inventory.getItemDetail(inv, slot))
  }

  val GET_ITEM_LIMIT = BasicMethod.of(
    "getItemLimit", "function(slot:number):number -- Get the maximum number of items which can be stored in this slot.",
    ::getItemLimit
  )
  private fun getItemLimit(unbaked: IUnbakedContext<Inventory>, args: IArguments): FutureMethodResult {
    val slot = args.getInt(0)
    val inv = unbaked.getInventoryStorage()
    return result(inventory.getItemLimit(inv, slot))
  }

  val PUSH_ITEMS = BasicMethod.of(
    "pushItems", "function(toName:string, fromSlot:number, [limit:number], [toSlot:number]):number " +
        "-- Push items from one inventory to another connected one.",
    ::pushItems
  )
  private fun pushItems(unbaked: IUnbakedContext<Inventory>, args: IArguments): FutureMethodResult {
    val toName = args.getString(0)
    val fromSlot = args.getInt(1)
    val limit = args.optInt(2)
    val toSlot = args.optInt(3)
    val (_, inv, access) = getContext(unbaked)
    return result(inventory.pushItems(inv, access, toName, fromSlot, limit, toSlot))
  }

  val PULL_ITEMS = BasicMethod.of(
    "pullItems", "function(fromName:string, fromSlot:number, [limit:number], [toSlot:number]):number " +
        "-- Pull items from a connected inventory into this one.",
    ::pullItems
  )
  private fun pullItems(unbaked: IUnbakedContext<Inventory>, args: IArguments): FutureMethodResult {
    val fromName = args.getString(0)
    val fromSlot = args.getInt(1)
    val limit = args.optInt(2)
    val toSlot = args.optInt(3)
    val (_, inv, access) = getContext(unbaked)
    return result(inventory.pullItems(inv, access, fromName, fromSlot, limit, toSlot))
  }

  private fun InventoryStorage.wrapped(): InventoryMethods.StorageWrapper =
    InventoryMethods.StorageWrapper(this)

  private fun IUnbakedContext<Inventory>.getInventory(): Inventory =
    bake().target

  private fun IUnbakedContext<Inventory>.getInventoryStorage(): InventoryMethods.StorageWrapper =
    InventoryStorage.of(getInventory(), null).wrapped()

  private fun getContext(unbaked: IUnbakedContext<Inventory>): Context {
    val ctx = unbaked.bake()
    val access = ContextHelpers.fromContext(ctx, IComputerAccess::class.java)
    return Context(ctx, InventoryStorage.of(ctx.target, null).wrapped(), access)
  }

  private data class Context(
    val context: IContext<Inventory>,
    val inv: InventoryMethods.StorageWrapper,
    val access: IComputerAccess?
  )
}
