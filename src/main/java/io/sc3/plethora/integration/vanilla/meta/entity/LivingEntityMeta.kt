package io.sc3.plethora.integration.vanilla.meta.entity

import io.sc3.plethora.api.meta.BaseMetaProvider
import io.sc3.plethora.api.method.ContextHelpers
import io.sc3.plethora.api.method.IPartialContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity

/**
 * A basic provider for living entities
 */
object LivingEntityMeta : BaseMetaProvider<LivingEntity>() {
  override fun getMeta(context: IPartialContext<LivingEntity>): Map<String, *> {
    return with (context.target) {
      mapOf(
        "armor" to mapOf(
          "boots"      to context.wrappedStack(EquipmentSlot.FEET),
          "leggings"   to context.wrappedStack(EquipmentSlot.LEGS),
          "chestplate" to context.wrappedStack(EquipmentSlot.CHEST),
          "helmet"     to context.wrappedStack(EquipmentSlot.HEAD),
        ),
        "heldItem"     to context.wrappedStack(EquipmentSlot.MAINHAND),
        "offhandItem"  to context.wrappedStack(EquipmentSlot.OFFHAND),

        "potionEffects" to activeStatusEffects.keys.map { it.name.string }.toList(),
        "health"        to health,
        "maxHealth"     to maxHealth,

        "isAirborne"     to !isOnGround,
        "isBurning"      to isOnFire,
        "isAlive"        to isAlive,
        "isInWater"      to isTouchingWater,
        "isOnLadder"     to isClimbing,
        "isSleeping"     to isSleeping,
        "isRiding"       to hasVehicle(),
        "isSneaking"     to isSneaking,
        "isSprinting"    to isSprinting,
        "isWet"          to isWet,
        "isChild"        to isBaby,
        "isDead"         to isDead,
        "isElytraFlying" to isFallFlying,
      )
    }
  }

  private fun IPartialContext<LivingEntity>.wrappedStack(slot: EquipmentSlot) =
    ContextHelpers.wrapStack(this, target.getEquippedStack(slot))
}
