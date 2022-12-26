package io.sc3.plethora

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import io.sc3.plethora.core.PlethoraCore
import io.sc3.plethora.gameplay.registry.Registration
import io.sc3.plethora.util.config.ConfigLoader.loadConfig
import io.sc3.plethora.util.config.ConfigValidator.ConfigValidationException
import io.sc3.plethora.util.config.ConfigValidator.validate
import io.sc3.plethora.util.config.PlethoraConfig

object Plethora : ModInitializer {
  internal const val modId = "plethora"
  @JvmStatic
  internal fun ModId(value: String) = Identifier(modId, value)

  @JvmField
  val log = LoggerFactory.getLogger("Plethora")!!

  @JvmField
  val config: PlethoraConfig = try {
    val path = FabricLoader.getInstance().configDir.resolve("plethora.hocon")
    loadConfig(PlethoraConfig::class.java, path).also { it.validate() }
  } catch (e: Exception) {
    when (e) {
      is ConfigValidationException -> { log.error("config/plethora.hocon is invalid!", e) }
      else -> { log.error("Failed to load config/plethora.hocon!", e) }
    }
    throw e
  }

  override fun onInitialize() {
    log.info("Plethora initializing")

    Registration.init()
    PlethoraCore.initializeCore()
  }
}
