package io.sc3.plethora.gameplay.registry

import io.sc3.plethora.Plethora.modId
import io.sc3.plethora.api.method.IMethod
import io.sc3.plethora.api.method.IMethodCollection
import io.sc3.plethora.api.method.IMethodRegistry
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.gameplay.modules.glasses.GlassesMethods
import io.sc3.plethora.gameplay.modules.glasses.canvas.Canvas2dMethods
import io.sc3.plethora.gameplay.modules.glasses.canvas.Canvas3dMethods
import io.sc3.plethora.gameplay.modules.glasses.objects.*
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectGroup.*
import io.sc3.plethora.gameplay.modules.glasses.objects.object2d.MultiPoint2d
import io.sc3.plethora.gameplay.modules.glasses.objects.object2d.MultiPointResizable2d
import io.sc3.plethora.gameplay.modules.glasses.objects.object2d.Positionable2d
import io.sc3.plethora.gameplay.modules.glasses.objects.object2d.Rectangle2d
import io.sc3.plethora.gameplay.modules.glasses.objects.object3d.*
import io.sc3.plethora.gameplay.modules.introspection.IntrospectionMethods
import io.sc3.plethora.gameplay.modules.kinetic.KineticMethods
import io.sc3.plethora.gameplay.modules.laser.LaserMethods
import io.sc3.plethora.gameplay.modules.scanner.ScannerMethods
import io.sc3.plethora.gameplay.modules.sensor.SensorMethods
import io.sc3.plethora.integration.CoreMethods
import io.sc3.plethora.integration.GetMetadataMethod

internal object PlethoraMethodRegistration {
  @JvmStatic
  fun registerMethods(r: IMethodRegistry) {
    with (r) {
      // Core methods
      methods(Any::class.java, GetMetadataMethod())
      methods(IModuleContainer::class.java, CoreMethods.LIST_MODULES, CoreMethods.HAS_MODULE,
        CoreMethods.FILTER_MODULES)
      methods(IMethodCollection::class.java, CoreMethods.GET_DOCS)

      // Modules
      moduleMethod("introspection:getID", IntrospectionMethods.GET_ID)
      moduleMethod("introspection:getName", IntrospectionMethods.GET_NAME)
      moduleMethod("introspection:getMetaOwner", IntrospectionMethods.GET_META_OWNER)
      moduleMethod("kinetic:launch", KineticMethods.LAUNCH)
      moduleMethod("laser:fire", LaserMethods.FIRE)
      moduleMethod("sensor:sense", SensorMethods.SENSE)
      moduleMethod("sensor:getMetaByID", SensorMethods.GET_META_BY_ID)
      moduleMethod("sensor:getMetaByName", SensorMethods.GET_META_BY_NAME)
      moduleMethod("scanner:sense", ScannerMethods.SCAN)
      moduleMethod("scanner:getBlockMeta", ScannerMethods.GET_BLOCK_META)

      // Overlay glasses
      moduleMethod("glasses:canvas", GlassesMethods.GET_CANVAS)
      moduleMethod("glasses:canvas3d", GlassesMethods.GET_CANVAS_3D)

      methods(ObjectGroup::class.java, GlassesMethods.CLEAR)
      methods(BaseObject::class.java, GlassesMethods.REMOVE)
      methods(Positionable2d::class.java, Positionable2d.GET_POSITION, Positionable2d.SET_POSITION)
      methods(Colourable::class.java, Colourable.GET_COLOUR, Colourable.GET_COLOR, Colourable.SET_COLOUR,
        Colourable.SET_COLOR, Colourable.GET_ALPHA, Colourable.SET_ALPHA)
      methods(Rectangle2d::class.java, Rectangle2d.GET_SIZE, Rectangle2d.SET_SIZE)
      methods(MultiPoint2d::class.java, MultiPoint2d.GET_POINT, MultiPoint2d.SET_POINT)
      methods(MultiPointResizable2d::class.java, MultiPointResizable2d.GET_POINT_COUNT,
        MultiPointResizable2d.REMOVE_POINT, MultiPointResizable2d.INSERT_POINT)
      methods(Scalable::class.java, Scalable.GET_SCALE, Scalable.SET_SCALE)
      methods(TextObject::class.java, TextObject.GET_TEXT, TextObject.SET_TEXT, TextObject.SET_SHADOW,
        TextObject.HAS_SHADOW, TextObject.GET_LINE_HEIGHT, TextObject.SET_LINE_HEIGHT)
      methods(ItemObject::class.java, ItemObject.GET_ITEM, ItemObject.SET_ITEM)
      methods(Group2d::class.java, Canvas2dMethods.ADD_RECTANGLE, Canvas2dMethods.ADD_LINE, Canvas2dMethods.ADD_DOT,
        Canvas2dMethods.ADD_TEXT, Canvas2dMethods.ADD_TRIANGLE, Canvas2dMethods.ADD_POLYGON, Canvas2dMethods.ADD_LINES,
        Canvas2dMethods.ADD_ITEM, Canvas2dMethods.ADD_GROUP)
      methods(Frame2d::class.java, Canvas2dMethods.GET_SIZE)

      methods(Positionable3d::class.java, Positionable3d.GET_POSITION, Positionable3d.SET_POSITION)
      methods(Rotatable3d::class.java, Rotatable3d.GET_ROTATION, Rotatable3d.SET_ROTATION)
      methods(DepthTestable::class.java, DepthTestable.IS_DEPTH_TESTED, DepthTestable.SET_DEPTH_TESTED)
      methods(Box3d::class.java, Box3d.GET_SIZE, Box3d.SET_SIZE)
      methods(ObjectRoot3d::class.java, ObjectRoot3d.RECENTER)
      methods(Origin3d::class.java, Canvas3dMethods.CREATE)
      methods(Group3d::class.java, Canvas3dMethods.ADD_FRAME, Canvas3dMethods.ADD_BOX, Canvas3dMethods.ADD_ITEM)
    }
  }

  private fun <T> IMethodRegistry.method(name: String, target: Class<T>, method: IMethod<T>) {
    registerMethod(modId, name, target, method)
  }

  @SafeVarargs
  private fun <T> IMethodRegistry.methods(target: Class<T>, vararg methods: IMethod<T>) {
    for (method in methods) {
      registerMethod(modId, method.name, target, method)
    }
  }

  private fun IMethodRegistry.moduleMethod(name: String, method: IMethod<IModuleContainer>) {
    method(name, IModuleContainer::class.java, method)
  }
}
