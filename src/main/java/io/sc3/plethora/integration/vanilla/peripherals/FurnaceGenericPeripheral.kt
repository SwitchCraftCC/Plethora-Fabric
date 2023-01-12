package io.sc3.plethora.integration.vanilla.peripherals

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.GenericPeripheral
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.util.Identifier

object FurnaceGenericPeripheral : GenericPeripheral {
  override fun id() = Identifier("furnace").toString()

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getRemainingBurnTime(furnace: AbstractFurnaceBlockEntity): MethodResult =
    MethodResult.of(furnace.burnTime)

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getBurnTime(furnace: AbstractFurnaceBlockEntity): MethodResult =
    MethodResult.of(furnace.fuelTime)

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getCookTime(furnace: AbstractFurnaceBlockEntity): MethodResult =
    MethodResult.of(furnace.cookTime)
}
