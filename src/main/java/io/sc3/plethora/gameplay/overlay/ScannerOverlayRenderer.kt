package io.sc3.plethora.gameplay.overlay

import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.LevelableModuleItem
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.ORES
import net.minecraft.block.*
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object ScannerOverlayRenderer : FlareOverlayRenderer() {
  private val cfg by Plethora.config::scanner

  private val blockColorCache = mutableMapOf<Block, FlareColor>()

  private val scanResults = mutableListOf<BlockResult>()
  private var scanTimer = 0f

  fun render(
    player: ClientPlayerEntity,
    stack: ItemStack,
    matrices: MatrixStack,
    tickDelta: Float,
    ticks: Float,
    camera: Camera
  ) {
    initFlareRenderer(matrices, camera)

    scanTimer += tickDelta
    if (scanTimer >= 10) {
      scanBlocks(player.getWorld(), player.blockPos, stack)
      scanTimer = 0f
    }

    for (result in scanResults) {
      renderFlare(matrices, camera, ticks, result.x + 0.5, result.y + 0.5, result.z + 0.5, result.color, 1.0f)
    }

    uninitFlareRenderer(matrices)
  }

  private fun scanBlocks(world: World, pos: BlockPos, stack: ItemStack) {
    // TODO: Move this to a scanning module class
    val x = pos.x; val y = pos.y; val z = pos.z
    val range = LevelableModuleItem.getEffectiveRange(stack)

    scanResults.clear()

    for (oX in x - range..x + range) {
      for (oY in y - range..y + range) {
        for (oZ in z - range..z + range) {
          val state = world.getBlockState(BlockPos(oX, oY, oZ))
          val block = state.block

          if (isBlockOre(state, block)) {
            scanResults.add(BlockResult(oX, oY, oZ, getFlareColorByBlock(block)))
          }
        }
      }
    }
  }

  private fun isBlockOre(state: BlockState?, block: Block?): Boolean {
    if (state == null || block == null || state.isAir) return false
    return block is ExperienceDroppingBlock
      || block is RedstoneOreBlock
      || block == Blocks.ANCIENT_DEBRIS
      || state.isIn(ORES)
  }

  private fun getFlareColorByBlock(block: Block) = blockColorCache.computeIfAbsent(block) {
    val id = Registries.BLOCK.getId(block)
    getFlareColorById(cfg.oreColours, id)
  }

  fun clearCache() {
    blockColorCache.clear()
    scanResults.clear()
    scanTimer = 0.0f
  }

  data class BlockResult(val x: Int, val y: Int, val z: Int, val color: FlareColor)
}
