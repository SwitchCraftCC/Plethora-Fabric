package pw.switchcraft.plethora.gameplay.modules.scanner

import net.minecraft.util.Identifier
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem
import pw.switchcraft.plethora.gameplay.registry.PlethoraModules.SCANNER_M

class ScannerModuleItem(settings: Settings) : LevelableModuleItem("scanner", settings) {
  private val cfg by Plethora.config::scanner

  override fun getModule(): Identifier = SCANNER_M

  override fun getBaseRange() = cfg.radius
  override fun getMaxRange() = cfg.maxRadius
  override fun getLevelCost() = cfg.scanLevelCost
}
