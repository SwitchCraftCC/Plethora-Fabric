package io.sc3.plethora.integration.vanilla.registry

import dan200.computercraft.api.ComputerCraftAPI
import io.sc3.plethora.integration.vanilla.peripherals.BrewingStandGenericPeripheral
import io.sc3.plethora.integration.vanilla.peripherals.FurnaceGenericPeripheral
import io.sc3.plethora.integration.vanilla.peripherals.SignGenericPeripheral

object VanillaPeripheralRegistration {
  @JvmStatic
  fun registerPeripherals() {
    ComputerCraftAPI.registerGenericSource(SignGenericPeripheral)
    ComputerCraftAPI.registerGenericSource(FurnaceGenericPeripheral)
    ComputerCraftAPI.registerGenericSource(BrewingStandGenericPeripheral)
  }
}
