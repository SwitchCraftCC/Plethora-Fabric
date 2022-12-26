package pw.switchcraft.plethora.gameplay.modules.sensor

import net.minecraft.util.Identifier
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem
import pw.switchcraft.plethora.gameplay.registry.PlethoraModules

class SensorModuleItem(settings: Settings) : LevelableModuleItem("sensor", settings) {
  private val cfg by Plethora.config::sensor

  override fun getModule(): Identifier = PlethoraModules.SENSOR_M

  override fun getBaseRange() = cfg.radius
  override fun getMaxRange() = cfg.maxRadius
  override fun getLevelCost() = cfg.senseLevelCost
}
