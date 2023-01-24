package io.sc3.plethora.core;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import io.sc3.plethora.api.PlethoraAPI;
import io.sc3.plethora.api.PlethoraEvents;
import io.sc3.plethora.core.executor.TaskRunner;

import static io.sc3.plethora.Plethora.log;

public class PlethoraCore {
    public static void initializeCore() {
        log.info("Plethora core initializing");

        log.info("Beginning Plethora registration");
        PlethoraEvents.REGISTER.invoker().onRegister(PlethoraAPI.instance());
        buildRegistries();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            log.info("Server started, resetting shared task runner");
            TaskRunner.SHARED.reset();
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            DefaultCostHandler.update();
            TaskRunner.SHARED.update();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            log.info("Server stopped, resetting cost handler and shared task runner");
            DefaultCostHandler.reset();
            TaskRunner.SHARED.reset();
        });
    }

    static void buildRegistries() {
        log.info("Building converter registry");
        ConverterRegistry.instance.build();
        log.info("Building meta registry");
        MetaRegistry.instance.build();
        log.info("Building method registry");
        MethodRegistry.instance.build();
    }
}
