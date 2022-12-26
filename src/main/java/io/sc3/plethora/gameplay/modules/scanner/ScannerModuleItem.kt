package io.sc3.plethora.gameplay.modules.scanner

import net.minecraft.util.Identifier
import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.LevelableModuleItem
import io.sc3.plethora.gameplay.registry.PlethoraModules.SCANNER_M

class ScannerModuleItem(settings: Settings) : LevelableModuleItem("scanner", settings) {
  private val cfg by Plethora.config::scanner

  override fun getModule(): Identifier = SCANNER_M

  override fun getBaseRange() = cfg.radius
  override fun getMaxRange() = cfg.maxRadius
  override fun getLevelCost() = cfg.scanLevelCost
}
