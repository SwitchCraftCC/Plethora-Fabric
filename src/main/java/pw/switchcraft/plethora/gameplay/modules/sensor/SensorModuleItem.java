package pw.switchcraft.plethora.gameplay.modules.sensor;

import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem;
import pw.switchcraft.plethora.util.config.Config.Sensor;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.SENSOR_M;

public class SensorModuleItem extends LevelableModuleItem {
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
        return SENSOR_M;
    }
}
