package io.sc3.plethora.gameplay.modules.sensor

import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.LevelableModuleItem
import io.sc3.plethora.gameplay.registry.PlethoraModules.SENSOR_M
import net.minecraft.util.Identifier

class SensorModuleItem(settings: Settings) : LevelableModuleItem("sensor", settings) {
  private val cfg by Plethora.config::sensor

  override fun getModule(): Identifier = SENSOR_M

  override val baseRange = cfg.radius
  override val maxRange  = cfg.maxRadius
  override val levelCost = cfg.senseLevelCost
}
