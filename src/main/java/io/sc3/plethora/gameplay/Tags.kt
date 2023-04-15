package io.sc3.plethora.gameplay

import io.sc3.plethora.Plethora.ModId
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object PlethoraBlockTags {
  val BLOCK_SCANNER_ORES = register("block_scanner_ores")
  val LASER_DONT_DROP = register("laser_dont_drop")

  private fun register(id: String): TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, ModId(id))
}
