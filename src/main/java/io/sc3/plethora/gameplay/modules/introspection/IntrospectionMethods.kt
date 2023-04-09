package io.sc3.plethora.gameplay.modules.introspection

import io.sc3.plethora.api.method.FutureMethodResult
import io.sc3.plethora.api.method.IUnbakedContext
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.SubtargetedModuleMethod
import io.sc3.plethora.gameplay.registry.PlethoraModules.INTROSPECTION_M
import io.sc3.plethora.gameplay.registry.PlethoraModules.SENSOR_M
import io.sc3.plethora.integration.EntityIdentifier

object IntrospectionMethods {
  val GET_ID = SubtargetedModuleMethod.of(
    "getID", INTROSPECTION_M, EntityIdentifier::class.java,
    "function():string -- Get this entity's UUID."
  ) { unbaked, _ -> getId(unbaked) }
  private fun getId(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val entity = IntrospectionContextHelpers.getContext(unbaked).entity
    return FutureMethodResult.result(entity.id.toString())
  }

  val GET_NAME = SubtargetedModuleMethod.of(
    "getName", INTROSPECTION_M, EntityIdentifier::class.java,
    "function():string -- Get this entity's name."
  ) { unbaked, _ -> getName(unbaked) }
  private fun getName(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val entity = IntrospectionContextHelpers.getContext(unbaked).entity
    return FutureMethodResult.result(entity.name)
  }

  val GET_META_OWNER = SubtargetedModuleMethod.of(
    "getMetaOwner", setOf(INTROSPECTION_M, SENSOR_M), EntityIdentifier::class.java,
    "function():table -- Get this entity's metadata."
  ) { unbaked, _ -> getMetaOwner(unbaked) }
  private fun getMetaOwner(unbaked: IUnbakedContext<IModuleContainer>): FutureMethodResult {
    val ctx = IntrospectionContextHelpers.getServerContext(unbaked)
    val entity = ctx.entity.getEntity(ctx.server)
    return FutureMethodResult.result(ctx.context.makePartialChild(entity).meta)
  }
}
