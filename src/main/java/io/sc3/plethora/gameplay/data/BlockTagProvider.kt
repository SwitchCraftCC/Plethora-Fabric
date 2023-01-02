package io.sc3.plethora.gameplay.data

import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.gameplay.registry.Registration.ModBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import java.util.concurrent.CompletableFuture

class BlockTagProvider(
  out: FabricDataOutput,
  future: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<Block>(out, RegistryKeys.BLOCK, future) {
  override fun configure(arg: RegistryWrapper.WrapperLookup) {
    getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(
      ModBlocks.MANIPULATOR_MARK_1,
      ModBlocks.MANIPULATOR_MARK_2,
      ModBlocks.REDSTONE_INTEGRATOR,
    )

    getOrCreateTagBuilder(BLOCK_SCANNER_ORES)
      .addOptionalTag(ConventionalBlockTags.ORES)
      // .add(Blocks.ANCIENT_DEBRIS)
  }

  companion object {
    val BLOCK_SCANNER_ORES: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, ModId("block_scanner_ores"))
  }
}

