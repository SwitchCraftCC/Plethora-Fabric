package pw.switchcraft.plethora

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import pw.switchcraft.plethora.core.PlethoraCore
import pw.switchcraft.plethora.gameplay.registry.Registration
import pw.switchcraft.plethora.util.config.ConfigLoader.loadConfig
import pw.switchcraft.plethora.util.config.ConfigValidator.ConfigValidationException
import pw.switchcraft.plethora.util.config.ConfigValidator.validate
import pw.switchcraft.plethora.util.config.PlethoraConfig

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
