package pw.switchcraft.plethora.util.config

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Files
import java.nio.file.Path

object ConfigLoader {
  private lateinit var loader: HoconConfigurationLoader
  private lateinit var rootNode: CommentedConfigurationNode

  @JvmStatic
  fun <T> loadConfig(configClass: Class<T>, path: Path): T {
    val config: T

    loader = HoconConfigurationLoader.builder()
      .path(path)
      .build()

    rootNode = loader.load()

    if (!Files.exists(path)) {
      config = configClass.getDeclaredConstructor().newInstance()
      rootNode.set(config)
      loader.save(rootNode)
    } else {
      config = rootNode.get(configClass)!!
    }

    return config
  }

  fun <T> saveConfig(config: T) {
    rootNode.set(config)
    loader.save(rootNode)
  }
}
