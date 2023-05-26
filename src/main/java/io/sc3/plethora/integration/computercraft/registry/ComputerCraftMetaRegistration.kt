package io.sc3.plethora.integration.computercraft.registry

import dan200.computercraft.api.ComputerCraftAPI
import io.sc3.plethora.api.meta.IMetaProvider
import io.sc3.plethora.api.meta.IMetaRegistry
import io.sc3.plethora.integration.computercraft.meta.item.*

object ComputerCraftMetaRegistration {
  @JvmStatic
  fun registerMetaProviders(registry: IMetaRegistry) {
    with (registry) {
      provider("computer", ComputerItemMeta())
      provider("media", MediaItemMeta())
      provider("pocket", PocketComputerItemMeta())
      provider("printout", PrintoutItemMeta())
      provider("turtle", TurtleItemMeta())
    }
  }

  private inline fun <reified T> IMetaRegistry.provider(name: String, provider: IMetaProvider<T>) {
    registerMetaProvider("${ComputerCraftAPI.MOD_ID}:$name", ComputerCraftAPI.MOD_ID, T::class.java, provider)
  }
}
