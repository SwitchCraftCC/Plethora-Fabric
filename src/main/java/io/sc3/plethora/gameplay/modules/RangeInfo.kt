package io.sc3.plethora.gameplay.modules

import io.sc3.plethora.api.reference.ConstantReference
import java.util.function.IntUnaryOperator

/**
 * Provides the range for a [SensorModuleItem] or [ScannerModuleItem]
 */
interface RangeInfo : ConstantReference<RangeInfo?> {
  /**
   * The maximum range this module operates at.
   *
   * @return This module's range.
   */
  val range: Int

  /**
   * The cost for some bulk operation (sense/scan).
   *
   * @return The cost of a bulk operation.
   * @see io.sc3.plethora.api.method.ICostHandler
   */
  val bulkCost: Int

  override fun get(): RangeInfo = this

  override fun safeGet(): RangeInfo = this

  companion object {
    fun of(level: Int, cost: IntUnaryOperator, range: IntUnaryOperator) = object : RangeInfo {
      override val range: Int
        get() = range.applyAsInt(level)
      override val bulkCost: Int
        get() = cost.applyAsInt(level)
    }
  }
}
