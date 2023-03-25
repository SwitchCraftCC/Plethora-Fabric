package io.sc3.plethora.integration.vanilla.peripherals

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.GenericPeripheral
import io.sc3.plethora.integration.vanilla.meta.blockentity.lines
import net.minecraft.block.Block
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.nio.charset.StandardCharsets

object SignGenericPeripheral : GenericPeripheral {
  override fun id() = Identifier("sign").toString()

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getSignText(sign: SignBlockEntity): MethodResult =
    MethodResult.of(sign.lines)

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun setSignText(sign: SignBlockEntity, args: IArguments) {
    val lines = (0 until 4).map {
      val line = args.optUtf8String(it) ?: ""
      // This may seem rather large, but it is possible to get quite large when using very narrow letters.
      if (line.length > 384) {
        throw LuaException("Expected length <= 384 for argument (${it + 1}), got ${line.length}")
      }

      Text.of(line)
    }

    // TODO: This currently bypasses text filtering, though I am not aware of any JE servers using it at the moment
    lines.forEachIndexed { i, line -> sign.setTextOnRow(i, line) }
    sign.markDirty()

    val world = sign.world ?: return
    val pos = sign.pos
    val state = world.getBlockState(pos)
    world.updateListeners(pos, state, state, Block.NOTIFY_ALL)
  }

  private fun IArguments.optUtf8String(index: Int): String? {
    val buf = optBytes(index).orElse(null) ?: return null
    return StandardCharsets.UTF_8.decode(buf).toString()
  }
}
