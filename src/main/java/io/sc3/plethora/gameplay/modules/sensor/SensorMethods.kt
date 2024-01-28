package io.sc3.plethora.gameplay.modules.sensor

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import io.sc3.plethora.Plethora
import io.sc3.plethora.api.IWorldLocation
import io.sc3.plethora.api.method.ContextKeys
import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IContext
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.SubtargetedModuleMethod
import io.sc3.plethora.api.reference.Reference
import io.sc3.plethora.core.ContextHelpers
import io.sc3.plethora.gameplay.modules.RangeInfo
import io.sc3.plethora.gameplay.modules.sensor.SensorHelpers.defaultPredicate
import io.sc3.plethora.gameplay.modules.sensor.SensorHelpers.findEntityByName
import io.sc3.plethora.gameplay.modules.sensor.SensorHelpers.findEntityByUuid
import io.sc3.plethora.gameplay.modules.sensor.SensorHelpers.getBox
import io.sc3.plethora.gameplay.registry.PlethoraModules.SENSOR_M
import io.sc3.plethora.integration.vanilla.meta.entity.EntityMeta.getBasicProperties
import net.minecraft.entity.Entity
import java.util.*

object SensorMethods {
  val SENSE = SubtargetedModuleMethod.of(
    "sense", SENSOR_M, IWorldLocation::class.java,
    "function():table -- Scan for entities in the vicinity",
    ::sense
  )
  private fun sense(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked)
    val world = ctx.loc.world
    val pos = ctx.loc.pos

    return ctx.context.costHandler.await(ctx.range.bulkCost.toDouble()) {
      val entities = world.getEntitiesByClass(
        Entity::class.java,
        getBox(pos, ctx.range.range.toDouble()),
        defaultPredicate
      )

      FutureMethodResult.result(entities.stream()
        .map { e -> getBasicProperties(e, ctx.loc) }
        .toList())
    }
  }

  val GET_META_BY_ID = SubtargetedModuleMethod.of(
    "getMetaByID", SENSOR_M, IWorldLocation::class.java,
    "function(id:string):table|nil -- Find a nearby entity by UUID",
    ::getMetaById
  )
  private fun getMetaById(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    val ctx = getContext(unbaked)
    val radius = ctx.range.range

    val uuid = try {
      UUID.fromString(args.getString(0))
    } catch (e: IllegalArgumentException) {
      throw LuaException("Invalid UUID")
    }

    val entity = findEntityByUuid(ctx.loc, radius.toDouble(), uuid)
      ?: return FutureMethodResult.empty()

    return FutureMethodResult.result(ctx.context.makeChild(
      entity,
      Reference.bounded(entity, ctx.loc, radius)
    ).meta)
  }

  val GET_META_BY_NAME = SubtargetedModuleMethod.of(
    "getMetaByName", SENSOR_M, IWorldLocation::class.java,
    "function(name:string):table|nil -- Find a nearby entity by name",
    ::getMetaByName
  )
  private fun getMetaByName(unbaked: IUnbakedContext<IModuleContainer>, args: IArguments): FutureMethodResult {
    try {
      val ctx = getContext(unbaked)
      val radius = ctx.range.range

      val entity = findEntityByName(ctx.loc, radius.toDouble(), args.getString(0))
        ?: return FutureMethodResult.empty()

      return FutureMethodResult.result(ctx.context.makeChild(
        entity,
        Reference.bounded(entity, ctx.loc, radius)
      ).meta)
    } catch (e: Exception) {
      Plethora.log.error("Error in getMetaByName", e)
      throw LuaException("Unknown error in getMetaByName")
    }
  }

  private fun getContext(unbaked: IUnbakedContext<IModuleContainer>): SensorMethodContext {
    val ctx = unbaked.bake()
    val loc = ContextHelpers.fromContext(ctx, IWorldLocation::class.java, ContextKeys.ORIGIN)
    val range = ContextHelpers.fromContext(ctx, RangeInfo::class.java, SENSOR_M)
    return SensorMethodContext(ctx, loc!!, range!!)
  }

  @JvmRecord
  private data class SensorMethodContext(
    val context: IContext<IModuleContainer>,
    val loc: IWorldLocation,
    val range: RangeInfo
  )
}
