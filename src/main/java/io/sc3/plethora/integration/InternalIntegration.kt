package io.sc3.plethora.integration

import io.sc3.plethora.api.PlethoraAPI.IPlethoraAPI
import io.sc3.plethora.api.meta.IMetaProvider
import io.sc3.plethora.api.meta.IMetaRegistry
import io.sc3.plethora.integration.scgoodies.ScGoodiesIntegration
import io.sc3.plethora.integration.scperipherals.ScPeripheralsIntegration
import net.fabricmc.loader.api.FabricLoader

abstract class InternalIntegration(val modId: String) {
  abstract fun init(api: IPlethoraAPI)

  protected inline fun <reified T> IMetaRegistry.provider(name: String, provider: IMetaProvider<T>) {
    registerMetaProvider("$modId:$name", modId, T::class.java, provider)
  }

  companion object {
    private val integrations = mapOf(
      "sc-goodies"     to ScGoodiesIntegration,
      "sc-peripherals" to ScPeripheralsIntegration,
    )

    fun init(api: IPlethoraAPI) {
      val loader = FabricLoader.getInstance()
      integrations.forEach { (modId, integration) ->
        if (loader.isModLoaded(modId)) integration.init(api)
      }
    }
  }
}
