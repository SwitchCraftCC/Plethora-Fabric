package io.sc3.plethora.gameplay.modules.sensor

import net.minecraft.util.Identifier
import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.LevelableModuleItem
import io.sc3.plethora.gameplay.registry.PlethoraModules

class SensorModuleItem(settings: Settings) : LevelableModuleItem("sensor", settings) {
  private val cfg by Plethora.config::sensor

  override fun getModule(): Identifier = PlethoraModules.SENSOR_M

  override fun getBaseRange() = cfg.radius
  override fun getMaxRange() = cfg.maxRadius
  override fun getLevelCost() = cfg.senseLevelCost
}
