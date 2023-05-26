package io.sc3.plethora.integration.vanilla.meta.block

import io.sc3.plethora.api.meta.BasicMetaProvider
import io.sc3.plethora.api.reference.BlockReference

object BlockReferenceMeta : BasicMetaProvider<BlockReference>(
  description = "Provides information about blocks which exist in the world."
) {
  override fun getMeta(target: BlockReference): Map<String, *> {
    val out: MutableMap<String, Any> = HashMap()

    val state = target.state
    val world = target.location.world
    val pos = target.location.pos

    out["hardness"] = state.getHardness(world, pos)

    val mapCol = state.getMapColor(world, pos)
    if (mapCol != null) {
      out["color"] = mapCol.color
      out["colour"] = mapCol.color
    }

    return out
  }
}
