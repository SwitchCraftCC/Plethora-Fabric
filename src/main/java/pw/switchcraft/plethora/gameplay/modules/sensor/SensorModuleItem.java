package pw.switchcraft.plethora.gameplay.modules.sensor;

import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem;
import pw.switchcraft.plethora.util.Config.Sensor;

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
}
