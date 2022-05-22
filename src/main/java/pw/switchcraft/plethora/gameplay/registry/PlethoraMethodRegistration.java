package pw.switchcraft.plethora.gameplay.registry;

import pw.switchcraft.plethora.api.method.IMethod;
import pw.switchcraft.plethora.api.method.IMethodCollection;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.gameplay.modules.introspection.IntrospectionMethods;
import pw.switchcraft.plethora.gameplay.modules.kinetic.KineticMethods;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserMethods;
import pw.switchcraft.plethora.gameplay.modules.scanner.ScannerMethods;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorMethods;
import pw.switchcraft.plethora.integration.CoreMethods;
import pw.switchcraft.plethora.integration.GetMetadataMethod;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

final class PlethoraMethodRegistration {
    static void registerMethods(IMethodRegistry r) {
        // Core methods
        method(r, Object.class, new GetMetadataMethod());
        method(r, IModuleContainer.class, CoreMethods.LIST_MODULES);
        method(r, IModuleContainer.class, CoreMethods.HAS_MODULE);
        method(r, IModuleContainer.class, CoreMethods.FILTER_MODULES);
        method(r, IMethodCollection.class, CoreMethods.GET_DOCS);

        // Modules
        moduleMethod(r, "introspection:getID", IntrospectionMethods.GET_ID);
        moduleMethod(r, "introspection:getName", IntrospectionMethods.GET_NAME);
        moduleMethod(r, "introspection:getMetaOwner", IntrospectionMethods.GET_META_OWNER);
        moduleMethod(r, "kinetic:launch", KineticMethods.LAUNCH);
        moduleMethod(r, "laser:fire", LaserMethods.FIRE);
        moduleMethod(r, "sensor:sense", SensorMethods.SENSE);
        moduleMethod(r, "sensor:getMetaByID", SensorMethods.GET_META_BY_ID);
        moduleMethod(r, "sensor:getMetaByName", SensorMethods.GET_META_BY_NAME);
        moduleMethod(r, "scanner:sense", ScannerMethods.SCAN);
        moduleMethod(r, "scanner:getBlockMeta", ScannerMethods.GET_BLOCK_META);
    }

    private static <T> void method(IMethodRegistry r, String name, Class<T> target, IMethod<T> method) {
        r.registerMethod(MOD_ID, name, target, method);
    }

    private static <T> void method(IMethodRegistry r, Class<T> target, IMethod<T> method) {
        r.registerMethod(MOD_ID, method.getName(), target, method);
    }

    private static void moduleMethod(IMethodRegistry r, String name, IMethod<IModuleContainer> method) {
        method(r, name, IModuleContainer.class, method);
    }
}
