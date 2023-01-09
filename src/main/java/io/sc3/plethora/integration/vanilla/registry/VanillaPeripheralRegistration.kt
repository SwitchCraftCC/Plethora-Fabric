package io.sc3.plethora.integration.vanilla.registry

import dan200.computercraft.api.ComputerCraftAPI
import io.sc3.plethora.integration.vanilla.peripherals.SignGenericPeripheral

object VanillaPeripheralRegistration {
  @JvmStatic
  fun registerPeripherals() {
    ComputerCraftAPI.registerGenericSource(SignGenericPeripheral)
  }
}
