package io.sc3.plethora.util

import io.sc3.plethora.Plethora
import io.sc3.plethora.api.reference.IReference
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.Inventory

abstract class LivingEntityInventoryWrapper<T : LivingEntity>(
  private val entityReference: IReference<T>
) {
  val safeInv: Inventory?
    get() {
      val inv = try {
        val entity = entityReference.get()
        provideInventory(entity)
      } catch (e: Exception) {
        Plethora.log.error("Error getting inventory for RangedInventoryWrapper", e)
        null
      }

      return inv
    }

  val inv: Inventory
    get() = safeInv ?: throw IllegalStateException("The inventory could not be provided")

  abstract fun provideInventory(entity: T): Inventory?
}
