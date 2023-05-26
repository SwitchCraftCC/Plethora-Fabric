package io.sc3.plethora.gameplay.modules.scanner

import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.LevelableModuleItem
import io.sc3.plethora.gameplay.registry.PlethoraModules.SCANNER_M
import net.minecraft.util.Identifier

class ScannerModuleItem(settings: Settings) : LevelableModuleItem("scanner", settings) {
  private val cfg by Plethora.config::scanner

  override fun getModule(): Identifier = SCANNER_M

  override val baseRange = cfg.radius
  override val maxRange  = cfg.maxRadius
  override val levelCost = cfg.scanLevelCost
}
