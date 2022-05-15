package pw.switchcraft.plethora.util.config;

import pw.switchcraft.plethora.util.config.Config.CostSystem;
import pw.switchcraft.plethora.util.config.Config.Scanner;
import pw.switchcraft.plethora.util.config.Config.Sensor;

import static pw.switchcraft.plethora.util.config.Config.Kinetic.*;
import static pw.switchcraft.plethora.util.config.Config.Laser.*;

public class ConfigValidator {
    public static void validate() throws ConfigValidationException {
        min("laser.minimumPotency", minimumPotency);
        min("laser.maximumPotency", maximumPotency);
        min("laser.cost", cost);
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

        min("costSystem.initial", CostSystem.initial);
        min("costSystem.regen", CostSystem.regen);
        min("costSystem.limit", CostSystem.limit);
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
