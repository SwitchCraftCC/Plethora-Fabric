package io.sc3.plethora.integration.vanilla.meta.blockentity

import io.sc3.plethora.api.meta.BasicMetaProvider
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.block.entity.SignText
import net.minecraft.text.Text

object SignBlockEntityMeta : BasicMetaProvider<SignBlockEntity>(
  description = "Provides the text upon the sign."
) {
  override fun getMeta(target: SignBlockEntity) =
    mapOf("lines" to target.lines)
}

val SignBlockEntity.lines: Map<Int, String>
  get() = (1 .. 8).associateWith { getText(it <= 4).getMessage((it - 1) % 4, true).string }

// Two different copies of messages here (non-filtered and filtered respectively)
// TODO: This currently bypasses text filtering, though I am not aware of any JE servers using it at the moment
fun SignText.withMessages(messages: List<Text>): SignText =
  SignText(messages.toTypedArray(), messages.toTypedArray(), color, isGlowing)
