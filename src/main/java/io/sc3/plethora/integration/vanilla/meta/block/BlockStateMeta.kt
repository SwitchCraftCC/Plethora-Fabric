package io.sc3.plethora.integration.vanilla.meta.block

import io.sc3.plethora.api.meta.BaseMetaProvider
import io.sc3.plethora.api.method.IPartialContext
import net.minecraft.block.BlockState

object BlockStateMeta : BaseMetaProvider<BlockState>(
  description = "Provides some very basic information about a block and its associated state."
) {
  override fun getMeta(context: IPartialContext<BlockState>): Map<String, *> {
    val state = context.target

    val data: MutableMap<String, Any> = HashMap()
    fillBasicMeta(data, state)

    val material = state.material
    data["material"] = context.makePartialChild(material).meta

    return data
  }

  @JvmStatic
  fun fillBasicMeta(data: MutableMap<in String, Any>, state: BlockState) {
    data["state"] = state.entries.entries.associate {
      val final = if (it.value !is String && it.value !is Number && it.value !is Boolean) {
        it.value.toString()
      } else {
        it.value
      }

      it.key.name to final
    }
  }
}
