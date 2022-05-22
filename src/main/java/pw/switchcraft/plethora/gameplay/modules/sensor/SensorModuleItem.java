package pw.switchcraft.plethora.gameplay.modules.sensor;

import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem;
import pw.switchcraft.plethora.util.config.Config.Sensor;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

public class SensorModuleItem extends LevelableModuleItem {
    public static final Identifier MODULE_ID = new Identifier(MOD_ID, "sensor");

    public SensorModuleItem(Settings settings) {
        super("sensor", settings);
    }

    @Override
    public int getBaseRange() {
        return Sensor.radius;
    }

    @Override
    public int getMaxRange() {
        return Sensor.maxRadius;
    }

    @Override
    public int getLevelCost() {
        return Sensor.senseLevelCost;
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return MODULE_ID;
    }
}
