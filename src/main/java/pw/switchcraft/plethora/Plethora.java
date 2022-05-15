package pw.switchcraft.plethora;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.switchcraft.plethora.core.PlethoraCore;
import pw.switchcraft.plethora.gameplay.registry.Registration;
import pw.switchcraft.plethora.util.config.Config;
import pw.switchcraft.plethora.util.config.ConfigLoader;
import pw.switchcraft.plethora.util.config.ConfigValidator;

public class Plethora implements ModInitializer {
    public static final String MOD_ID = "plethora";

    public static final Logger LOG = LoggerFactory.getLogger("Plethora");
    public static Config CONFIG;

    @Override
    public void onInitialize() {
        LOG.info("Plethora initializing");

        try {
            CONFIG = ConfigLoader.loadConfig(Config.class, FabricLoader.getInstance().getConfigDir()
                .resolve("plethora").resolve("plethora.hocon"));

            ConfigValidator.validate();
        } catch (ConfigValidator.ConfigValidationException e) {
            LOG.error("config/plethora/plethora.hocon is invalid", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Registration.init();
        PlethoraCore.initializeCore();
    }
}
