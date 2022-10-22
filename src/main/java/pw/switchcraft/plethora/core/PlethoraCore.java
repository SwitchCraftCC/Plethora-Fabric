package pw.switchcraft.plethora.core;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import pw.switchcraft.plethora.api.PlethoraAPI;
import pw.switchcraft.plethora.api.PlethoraEvents;
import pw.switchcraft.plethora.core.executor.TaskRunner;

import static pw.switchcraft.plethora.Plethora.log;

public class PlethoraCore {
    public static void initializeCore() {
        log.info("Plethora core initializing");

        log.debug("Building registries");
        PlethoraEvents.REGISTER.invoker().onRegister(PlethoraAPI.instance());
        buildRegistries();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            log.debug("Server started, resetting shared task runner");
            TaskRunner.SHARED.reset();
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            DefaultCostHandler.update();
            TaskRunner.SHARED.update();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            log.debug("Server stopped, resetting cost handler and shared task runner");
            DefaultCostHandler.reset();
            TaskRunner.SHARED.reset();
        });
    }

    static void buildRegistries() {
        ConverterRegistry.instance.build();
        MetaRegistry.instance.build();
        MethodRegistry.instance.build();
    }
}
