package pw.switchcraft.plethora.api.method;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.Hand;

import java.util.Locale;

/**
 * Various helpers for arguments. These are available in
 * dan200.computercraft.shared.peripheral.generic.methods.ArgumentHelpers but are not exported in the public API yet.
 */
public final class ArgumentHelper {
    public static void assertBetween(double value, double min, double max, String message) throws LuaException {
        if (value < min || value > max || Double.isNaN(value)) {
            throw new LuaException(String.format(message, "between " + min + " and " + max));
        }
    }

    public static void assertBetween(int value, int min, int max, String message) throws LuaException {
        if (value < min || value > max) {
            throw new LuaException(String.format(message, "between " + min + " and " + max));
        }
    }

    public static Hand optHand(IArguments args, int index) throws LuaException {
        String hand = args.optString(index, "main").toLowerCase(Locale.ENGLISH);
        return switch (hand) {
            case "main", "mainhand", "right" -> Hand.MAIN_HAND;
            case "off", "offhand", "left" -> Hand.OFF_HAND;
            default -> throw new LuaException("Unknown hand '" + hand + "', expected 'main' or 'off'");
        };
    }
}
