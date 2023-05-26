package io.sc3.plethora.integration.vanilla.converter

import io.sc3.plethora.api.WorldLocation
import io.sc3.plethora.api.converter.ConstantConverter
import io.sc3.plethora.api.converter.DynamicConverter
import io.sc3.plethora.api.reference.BlockReference
import io.sc3.plethora.integration.EntityIdentifier
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

object VanillaConverters {
  val GET_STACK_ITEM = ConstantConverter { obj: ItemStack -> obj.item }

  val GET_BLOCK_REFERENCE_BLOCK = DynamicConverter { obj: BlockReference -> obj.state }
  val GET_BLOCK_REFERENCE_BLOCK_ENTITY = ConstantConverter { obj: BlockReference -> obj.blockEntity }
  val GET_BLOCK_ENTITY_REFERENCE = ConstantConverter { from: BlockEntity ->
    val world = from.world
    val pos = from.pos
    if (world != null && pos != null && world.getBlockEntity(pos) === from) {
      BlockReference(WorldLocation(world, pos), world.getBlockState(pos), from)
    } else {
      null
    }
  }

  val GET_BLOCK_STATE_BLOCK = ConstantConverter { obj: BlockState -> obj.block }

  val GET_ENTITY_IDENTIFIER = ConstantConverter { from: Entity ->
    if (from is PlayerEntity) {
      EntityIdentifier.Player(from.gameProfile)
    } else {
      EntityIdentifier(from)
    }
  }
}
