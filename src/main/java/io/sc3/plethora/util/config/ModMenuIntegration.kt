package io.sc3.plethora.util.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.overlay.ScannerOverlayRenderer
import io.sc3.plethora.gameplay.overlay.SensorOverlayRenderer
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags.ORES
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.ExperienceDroppingBlock
import net.minecraft.block.RedstoneOreBlock
import net.minecraft.registry.Registries
import net.minecraft.text.Text.of
import net.minecraft.text.Text.translatable
import java.awt.Color

object ModMenuIntegration : ModMenuApi {
  private const val key = "gui.plethora.config"
  private val cfg
    get() = Plethora.config

  override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
    return ConfigScreenFactory { parent ->
      val builder = ConfigBuilder.create().setParentScreen(parent)
        .setTitle(translatable("$key.title"))
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
      val scanner = builder.getOrCreateCategory(translatable("$key.scanner.title"))

      val oreColours = builder.entryBuilder().startSubCategory(translatable("$key.scanner.ore_colours"))
      oreColours.addAll(getOreBlocks().map { block ->
        val id = Registries.BLOCK.getId(block).toString()
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

  private fun getOreBlocks() = Registries.BLOCK
    .filter { it is ExperienceDroppingBlock || it is RedstoneOreBlock || it.defaultState.isIn(ORES) }

  private fun String.color() = Color.decode(this).rgb and 0xFFFFFF
}
