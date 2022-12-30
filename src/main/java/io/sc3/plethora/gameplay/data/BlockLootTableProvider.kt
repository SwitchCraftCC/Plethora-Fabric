package io.sc3.plethora.gameplay.data

import io.sc3.plethora.gameplay.registry.Registration.ModBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider

class BlockLootTableProvider(out: FabricDataOutput) : FabricBlockLootTableProvider(out) {
  override fun generate() {
    addDrop(ModBlocks.MANIPULATOR_MARK_1)
    addDrop(ModBlocks.MANIPULATOR_MARK_2)
    addDrop(ModBlocks.REDSTONE_INTEGRATOR)
  }
}
