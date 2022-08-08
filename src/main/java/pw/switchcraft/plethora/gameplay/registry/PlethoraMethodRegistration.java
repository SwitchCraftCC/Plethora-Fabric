package pw.switchcraft.plethora.gameplay.registry;

import pw.switchcraft.plethora.api.method.IMethod;
import pw.switchcraft.plethora.api.method.IMethodCollection;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.gameplay.modules.glasses.GlassesMethods;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.Canvas2dMethods;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.Canvas3dMethods;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.*;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.MultiPoint2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.MultiPointResizable2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.Positionable2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.Rectangle;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d.*;
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

        // Overlay glasses
        moduleMethod(r, "glasses:canvas", GlassesMethods.GET_CANVAS);
        moduleMethod(r, "glasses:canvas3d", GlassesMethods.GET_CANVAS_3D);

        method(r, ObjectGroup.class, GlassesMethods.CLEAR);
        method(r, BaseObject.class, GlassesMethods.REMOVE);
        methods(r, Positionable2d.class, Positionable2d.GET_POSITION, Positionable2d.SET_POSITION);
        methods(r, Colourable.class, Colourable.GET_COLOUR, Colourable.GET_COLOR, Colourable.SET_COLOUR,
            Colourable.SET_COLOR, Colourable.GET_ALPHA, Colourable.SET_ALPHA);
        methods(r, Rectangle.class, Rectangle.GET_SIZE, Rectangle.SET_SIZE);
        methods(r, MultiPoint2d.class, MultiPoint2d.GET_POINT, MultiPoint2d.SET_POINT);
        methods(r, MultiPointResizable2d.class, MultiPointResizable2d.GET_POINT_COUNT,
            MultiPointResizable2d.REMOVE_POINT, MultiPointResizable2d.INSERT_POINT);
        methods(r, Scalable.class, Scalable.GET_SCALE, Scalable.SET_SCALE);
        methods(r, TextObject.class, TextObject.GET_TEXT, TextObject.SET_TEXT, TextObject.SET_SHADOW,
            TextObject.HAS_SHADOW, TextObject.GET_LINE_HEIGHT, TextObject.SET_LINE_HEIGHT);
        methods(r, ItemObject.class, ItemObject.GET_ITEM, ItemObject.SET_ITEM);

        methods(r, ObjectGroup.Group2d.class, Canvas2dMethods.ADD_RECTANGLE, Canvas2dMethods.ADD_LINE,
            Canvas2dMethods.ADD_DOT, Canvas2dMethods.ADD_TEXT, Canvas2dMethods.ADD_TRIANGLE,
            Canvas2dMethods.ADD_POLYGON, Canvas2dMethods.ADD_LINES, Canvas2dMethods.ADD_ITEM,
            Canvas2dMethods.ADD_GROUP);
        methods(r, ObjectGroup.Frame2d.class, Canvas2dMethods.GET_SIZE);

        methods(r, Positionable3d.class, Positionable3d.GET_POSITION, Positionable3d.SET_POSITION);
        methods(r, Rotatable3d.class, Rotatable3d.GET_ROTATION, Rotatable3d.SET_ROTATION);
        methods(r, DepthTestable.class, DepthTestable.IS_DEPTH_TESTED, DepthTestable.SET_DEPTH_TESTED);
        methods(r, Box.class, Box.GET_SIZE, Box.SET_SIZE);
        methods(r, ObjectRoot3d.class, ObjectRoot3d.RECENTER);
        methods(r, ObjectGroup.Origin3d.class, Canvas3dMethods.CREATE);
        methods(r, ObjectGroup.Group3d.class, Canvas3dMethods.ADD_BOX);
    }

    private static <T> void method(IMethodRegistry r, String name, Class<T> target, IMethod<T> method) {
        r.registerMethod(MOD_ID, name, target, method);
    }

    private static <T> void method(IMethodRegistry r, Class<T> target, IMethod<T> method) {
        r.registerMethod(MOD_ID, method.getName(), target, method);
    }

    @SafeVarargs
    private static <T> void methods(IMethodRegistry r, Class<T> target, IMethod<T>... methods) {
        for (IMethod<T> method : methods) {
            method(r, target, method);
        }
    }

    private static void moduleMethod(IMethodRegistry r, String name, IMethod<IModuleContainer> method) {
        method(r, name, IModuleContainer.class, method);
    }
}
