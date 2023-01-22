package io.sc3.plethora.integration.vanilla.method

import dan200.computercraft.api.lua.IArguments
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.FutureMethodResult.result
import io.sc3.plethora.api.method.IContext
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.SubtargetedModuleMethod
import io.sc3.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.getPlayerContext
import io.sc3.plethora.gameplay.registry.PlethoraModules.INTROSPECTION_M
import io.sc3.plethora.integration.EntityIdentifier
import io.sc3.plethora.util.EquipmentInventoryWrapper
import io.sc3.plethora.util.RangedInventoryWrapper
import net.minecraft.entity.player.PlayerInventory.MAIN_SIZE
import net.minecraft.inventory.Inventory

object EntityIntrospectionMethods {
  val GET_INVENTORY = SubtargetedModuleMethod.of(
    "getInventory", INTROSPECTION_M, EntityIdentifier.Player::class.java,
    "function():table -- Get this player's inventory",
    ::getInventory
  )
  private fun getInventory(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val ctx = getPlayerContext(unbaked)
    val player = ctx.player.getPlayer(ctx.server)
    val inventory = RangedInventoryWrapper(player.inventory, 0, MAIN_SIZE)
    return inventory.wrapped(ctx.context)
  }

  val GET_EQUIPMENT = SubtargetedModuleMethod.of(
    "getEquipment", INTROSPECTION_M, EntityIdentifier.Player::class.java,
    "function():table -- Get this player's held item and armor",
    ::getEquipment
  )
  private fun getEquipment(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val ctx = getPlayerContext(unbaked)
    val player = ctx.player.getPlayer(ctx.server)
    val equipment = EquipmentInventoryWrapper(player)
    return equipment.wrapped(ctx.context)
  }

  val GET_ENDER_CHEST = SubtargetedModuleMethod.of(
    "getEnder", INTROSPECTION_M, EntityIdentifier.Player::class.java,
    "function():table -- Get this player's ender chest",
    ::getEnder
  )
  private fun getEnder(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val ctx = getPlayerContext(unbaked)
    val player = ctx.player.getPlayer(ctx.server)
    return player.enderChestInventory.wrapped(ctx.context)
  }

  private fun Inventory.wrapped(ctx: IContext<*>): FutureMethodResult {
    // Wrap the Inventory with Plethora's methods (see InventoryMethodsWrapper)
    val obj = ctx.makeChildId(this).getObject()
    return result(if (obj.methodNames.isEmpty()) null else obj)
  }
}
