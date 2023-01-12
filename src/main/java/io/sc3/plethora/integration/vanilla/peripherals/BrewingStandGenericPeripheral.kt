package io.sc3.plethora.integration.vanilla.peripherals

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.GenericPeripheral
import net.minecraft.block.entity.BrewingStandBlockEntity
import net.minecraft.util.Identifier

object BrewingStandGenericPeripheral : GenericPeripheral {
  override fun id() = Identifier("brewing_stand").toString()

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getBrewTime(brewingStand: BrewingStandBlockEntity): MethodResult =
    MethodResult.of(brewingStand.brewTime)
}
