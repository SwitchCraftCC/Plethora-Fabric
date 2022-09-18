package pw.switchcraft.plethora

import net.minecraft.util.Identifier

object PlethoraKt {
  internal const val modId = Plethora.MOD_ID
  internal fun ModId(value: String) = Identifier(modId, value)
}
