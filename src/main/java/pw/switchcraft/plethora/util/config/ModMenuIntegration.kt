package pw.switchcraft.plethora.util.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.ORES
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.OreBlock
import net.minecraft.block.RedstoneOreBlock
import net.minecraft.text.Text.of
import net.minecraft.text.Text.translatable
import net.minecraft.util.registry.Registry
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.gameplay.overlay.ScannerOverlayRenderer
import pw.switchcraft.plethora.gameplay.overlay.SensorOverlayRenderer
import java.awt.Color

object ModMenuIntegration : ModMenuApi {
  private const val lang = "gui.plethora.config"
  private val cfg by Plethora::config

  override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
    return ConfigScreenFactory { parent ->
      val builder = ConfigBuilder.create().setParentScreen(parent)
        .setTitle(translatable("$lang.title"))
        .setSavingRunnable {
          ConfigLoader.saveConfig(cfg)

          if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ScannerOverlayRenderer.clearCache()
            SensorOverlayRenderer.clearCache()
          }
        }

      // ======================================================================
      // Scanner
      // ======================================================================
      val scanner = builder.getOrCreateCategory(translatable("$lang.scanner.title"))

      val oreColours = builder.entryBuilder().startSubCategory(translatable("$lang.scanner.ore_colours"))
      oreColours.addAll(getOreBlocks().map { block ->
        val id = Registry.BLOCK.getId(block).toString()
        val currentColor = (cfg.scanner.oreColours[id] ?: "#FFFFFF").color()
        val defaultColor = (PlethoraConfig.Scanner.defaultOreColors[id] ?: "#FFFFFF").color()
        builder.entryBuilder()
          .startColorField(of(id), currentColor)
          .setDefaultValue(defaultColor)
          .setSaveConsumer { cfg.scanner.oreColours[id] = String.format("#%06X", 0xFFFFFF and it) }
          .build()
      })
      scanner.addEntry(oreColours.build())

      builder.build()
    }
  }

  private fun getOreBlocks() = Registry.BLOCK
    .filter { it is OreBlock || it is RedstoneOreBlock || it.defaultState.isIn(ORES) }

  private fun String.color() = Color.decode(this).rgb and 0xFFFFFF
}
