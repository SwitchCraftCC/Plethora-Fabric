package io.sc3.plethora.gameplay.registry;

import net.minecraft.util.Identifier;
import io.sc3.plethora.Plethora;

public class PlethoraModules {
    private static final String MOD_ID = Plethora.modId;

    public static final String INTROSPECTION = "introspection";
    public static final String LASER         = "laser";
    public static final String SCANNER       = "scanner";
    public static final String SENSOR        = "sensor";
    public static final String KINETIC       = "kinetic";
    public static final String KEYBOARD      = "keyboard";
    public static final String GLASSES       = "glasses";
    // public static final String CHAT          = "chat";
    // public static final String CHAT_CREATIVE = "chat_creative";

    public static final String INTROSPECTION_S = MOD_ID + ":" + INTROSPECTION;
    public static final String KINETIC_S       = MOD_ID + ":" + KINETIC;
    public static final String LASER_S         = MOD_ID + ":" + LASER;
    public static final String SCANNER_S       = MOD_ID + ":" + SCANNER;
    public static final String SENSOR_S        = MOD_ID + ":" + SENSOR;
    public static final String KEYBOARD_S      = MOD_ID + ":" + KEYBOARD;
    public static final String GLASSES_S       = MOD_ID + ":" + GLASSES;
    // public static final String CHAT_S          = MOD_ID + ":" + CHAT;
    // public static final String CHAT_CREATIVE_S = MOD_ID + ":" + CHAT_CREATIVE;

    public static final Identifier INTROSPECTION_M = new Identifier(MOD_ID, INTROSPECTION);
    public static final Identifier KINETIC_M       = new Identifier(MOD_ID, KINETIC);
    public static final Identifier LASER_M         = new Identifier(MOD_ID, LASER);
    public static final Identifier SCANNER_M       = new Identifier(MOD_ID, SCANNER);
    public static final Identifier SENSOR_M        = new Identifier(MOD_ID, SENSOR);
    public static final Identifier KEYBOARD_M      = new Identifier(MOD_ID, KEYBOARD);
    public static final Identifier GLASSES_M       = new Identifier(MOD_ID, GLASSES);
    // public static final Identifier CHAT_M          = new Identifier(MOD_ID, CHAT);
    // public static final Identifier CHAT_CREATIVE_M = new Identifier(MOD_ID, CHAT_CREATIVE);

    private static final String[] NAMES = new String[]{
        INTROSPECTION, LASER, SCANNER, SENSOR, KINETIC, KEYBOARD, GLASSES/*, CHAT, CHAT_CREATIVE*/
    };

    public static final Identifier[] TURTLE_MODULES = new Identifier[]{
        INTROSPECTION_M,
        LASER_M,
        SCANNER_M,
        SENSOR_M,
        // CHAT_CREATIVE_M,
    };

    public static final Identifier[] POCKET_MODULES = new Identifier[]{
        LASER_M,
        SCANNER_M,
        SENSOR_M,
        INTROSPECTION_M,
        KINETIC_M,
        // CHAT_M,
        // CHAT_CREATIVE_M,
    };

    public static final Identifier[] VEHICLE_MODULES = new Identifier[]{
        LASER_M,
        SCANNER_M,
        SENSOR_M,
        INTROSPECTION_M,
        KINETIC_M,
        // CHAT_CREATIVE_M,
    };
}
