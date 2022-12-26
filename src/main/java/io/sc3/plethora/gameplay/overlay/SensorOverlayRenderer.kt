package io.sc3.plethora.gameplay.overlay

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Camera
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.TypeFilter
import net.minecraft.util.math.Box
import io.sc3.plethora.Plethora
import io.sc3.plethora.gameplay.modules.LevelableModuleItem

object SensorOverlayRenderer : FlareOverlayRenderer() {
  private val cfg by Plethora.config::sensor

  private val entityColorCache: MutableMap<EntityType<*>, FlareColor> = HashMap()

  fun render(
    player: ClientPlayerEntity,
    stack: ItemStack,
    matrices: MatrixStack,
    ticks: Float,
    camera: Camera
  ) {
    initFlareRenderer(matrices, camera)

    val world = player.getWorld()
    val position = player.eyePos
    val range = LevelableModuleItem.getEffectiveRange(stack)

    // TODO: Rate limit scanning for these too?
    val entities = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity::class.java), Box(
      position.x - range, position.y - range, position.z - range,
      position.x + range, position.y + range, position.z + range
    )) { it !== player }

    for (entity in entities) {
      val pos = entity.pos
      val color = getFlareColorByEntity(entity.type)
      renderFlare(matrices, camera, ticks, pos.x, pos.y + entity.height / 2, pos.z, color, 1.0f)
    }

    uninitFlareRenderer(matrices)
  }

  private fun getFlareColorByEntity(entity: EntityType<*>) = entityColorCache.computeIfAbsent(entity) {
    val id = Registries.ENTITY_TYPE.getId(entity)
    getFlareColorById(cfg.entityColours, id)
  }

  fun clearCache() { entityColorCache.clear() }
}
