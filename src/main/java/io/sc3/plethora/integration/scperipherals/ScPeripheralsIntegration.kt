package io.sc3.plethora.integration.scperipherals

import io.sc3.plethora.api.PlethoraAPI
import io.sc3.plethora.integration.InternalIntegration

object ScPeripheralsIntegration : InternalIntegration("sc-peripherals") {
  override fun init(api: PlethoraAPI.IPlethoraAPI) {
    with (api.metaRegistry()) {
      provider("printItem", PrintItemMeta())
      provider("posterItem", PosterItemMeta())
    }
  }
}
