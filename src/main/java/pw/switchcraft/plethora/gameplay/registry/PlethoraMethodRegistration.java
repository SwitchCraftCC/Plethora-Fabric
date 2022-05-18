package pw.switchcraft.plethora.gameplay.registry;

import pw.switchcraft.plethora.api.method.IMethod;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserMethods;
import pw.switchcraft.plethora.gameplay.modules.scanner.ScannerMethods;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorMethods;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

final class PlethoraMethodRegistration {
    static void registerMethods(IMethodRegistry r) {
        moduleMethod(r, "laser:fire", LaserMethods.FIRE);
        moduleMethod(r, "sensor:sense", SensorMethods.SENSE);
        moduleMethod(r, "sensor:getMetaByID", SensorMethods.GET_META_BY_ID);
        moduleMethod(r, "sensor:getMetaByName", SensorMethods.GET_META_BY_NAME);
        moduleMethod(r, "scanner:sense", ScannerMethods.SCAN);
        moduleMethod(r, "scanner:getBlockMeta", ScannerMethods.GET_BLOCK_META);
    }

    private static void moduleMethod(IMethodRegistry r, String name, IMethod<IModuleContainer> method) {
        r.registerMethod(MOD_ID, name, IModuleContainer.class, method);
    }
}
