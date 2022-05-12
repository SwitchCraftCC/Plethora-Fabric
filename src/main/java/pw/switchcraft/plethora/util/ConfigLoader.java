package pw.switchcraft.plethora.util;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    public static <T> T loadConfig(
        Class<T> configClass,
        Path path
    ) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T config;
        CommentedConfigurationNode rootNode;

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .path(path)
            .build();

        rootNode = loader.load();

        if (!Files.exists(path)) {
            config = configClass.getDeclaredConstructor().newInstance();
            rootNode.set(config);
        } else {
            config = rootNode.get(configClass);
        }

        loader.save(rootNode);

        return config;
    }
}
