package pw.switchcraft.plethora.util;

import pw.switchcraft.plethora.util.Config.Scanner;
import pw.switchcraft.plethora.util.Config.Sensor;

import static pw.switchcraft.plethora.util.Config.Kinetic.*;
import static pw.switchcraft.plethora.util.Config.Laser.*;

public class ConfigValidator {
    public static void validate() throws ConfigValidationException {
        min("laser.minimumPotency", minimumPotency);
        min("laser.maximumPotency", maximumPotency);
        min("laser.damage", damage);
        min("laser.lifetime", lifetime);
        min("kinetic.launchMax", launchMax);
        min("kinetic.launchYScale", launchYScale);
        min("kinetic.launchElytraScale", launchElytraScale);
        min("scanner.radius", Scanner.radius);
        min("scanner.maxRadius", Scanner.maxRadius);
        min("scanner.scanLevelCost", Scanner.scanLevelCost);
        min("sensor.radius", Sensor.radius);
        min("sensor.maxRadius", Sensor.maxRadius);
        min("sensor.senseLevelCost", Sensor.senseLevelCost);
    }

    public static class ConfigValidationException extends RuntimeException {
        public ConfigValidationException(String option) {
            super("Invalid value for " + option);
        }
    }

    public static void min(String name, double value) throws ConfigValidationException {
        if (value < 0) throw new ConfigValidationException(name);
    }
}
