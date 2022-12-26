package io.sc3.plethora.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class PlethoraEvents {
    public static final Event<Register> REGISTER = EventFactory.createArrayBacked(Register.class, listeners -> api -> {
       for (Register l : listeners) {
           l.onRegister(api);
       }
    });

    @FunctionalInterface
    public interface Register {
        void onRegister(PlethoraAPI.IPlethoraAPI api);
    }
}
