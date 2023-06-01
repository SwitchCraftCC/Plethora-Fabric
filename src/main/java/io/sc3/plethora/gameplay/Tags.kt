package io.sc3.plethora.gameplay

import io.sc3.plethora.Plethora.ModId
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys.BLOCK
import net.minecraft.registry.RegistryKeys.ENTITY_TYPE
import net.minecraft.registry.tag.TagKey

object PlethoraBlockTags {
  val BLOCK_SCANNER_ORES = register("block_scanner_ores")
  val LASER_DONT_DROP = register("laser_dont_drop")

  private fun register(id: String): TagKey<Block> = TagKey.of(BLOCK, ModId(id))
}

object PlethoraEntityTags {
  val LASERS_PROVIDE_ENERGY = register("lasers_provide_energy")

  private fun register(id: String): TagKey<EntityType<*>> = TagKey.of(ENTITY_TYPE, ModId(id))
}
