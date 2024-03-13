package io.sc3.plethora.integration.scgoodies

import io.sc3.plethora.api.PlethoraAPI
import io.sc3.plethora.integration.InternalIntegration

object ScGoodiesIntegration : InternalIntegration("sc-goodies") {
  override fun init(api: PlethoraAPI.IPlethoraAPI) {
    with (api.metaRegistry()) {
      provider("magnetItem", MagnetItemMeta())
    }
  }
}
