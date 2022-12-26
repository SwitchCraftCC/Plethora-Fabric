package io.sc3.plethora.util.config

object ConfigValidator {
  @Throws(ConfigValidationException::class)
  fun PlethoraConfig.validate() {
    min("laser.minimumPotency", laser.minimumPotency)
    min("laser.maximumPotency", laser.maximumPotency)
    min("laser.cost", laser.cost)
    min("laser.damage", laser.damage)
    min("laser.lifetime", laser.lifetime.toDouble())

    min("kinetic.launchMax", kinetic.launchMax.toDouble())
    min("kinetic.launchCost", kinetic.launchCost.toDouble())
    min("kinetic.launchYScale", kinetic.launchYScale)
    min("kinetic.launchElytraScale", kinetic.launchElytraScale)

    min("scanner.radius", scanner.radius.toDouble())
    min("scanner.maxRadius", scanner.maxRadius.toDouble())
    min("scanner.scanLevelCost", scanner.scanLevelCost.toDouble())

    min("sensor.radius", sensor.radius.toDouble())
    min("sensor.maxRadius", sensor.maxRadius.toDouble())
    min("sensor.senseLevelCost", sensor.senseLevelCost.toDouble())

    min("costSystem.initial", costSystem.initial)
    min("costSystem.regen", costSystem.regen)
    min("costSystem.limit", costSystem.limit)
  }

  @Throws(ConfigValidationException::class)
  fun min(name: String, value: Double) {
    if (value < 0) throw ConfigValidationException(name)
  }

  class ConfigValidationException(option: String) : RuntimeException("Invalid value for $option")
}
