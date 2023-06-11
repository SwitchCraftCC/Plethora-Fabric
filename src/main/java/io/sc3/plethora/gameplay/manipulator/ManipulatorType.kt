package io.sc3.plethora.gameplay.manipulator

import io.sc3.library.ext.rotate
import io.sc3.library.ext.toDiv16
import io.sc3.library.ext.toMul16
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlock.Companion.OFFSET
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlock.Companion.PIX
import net.minecraft.item.ItemStack
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import java.util.*

enum class ManipulatorType(
  val scale: Float,
  private vararg val boxes: Box
) : StringIdentifiable {
  MARK_1(
    0.5f,
    Box(PIX * 5, OFFSET, PIX * 5, PIX * 11, OFFSET + PIX, PIX * 11)
  ),
  MARK_2(
    0.25f,
    Box(PIX * 3, OFFSET, PIX * 3, PIX * 5, OFFSET + PIX, PIX * 5),
    Box(PIX * 3, OFFSET, PIX * 11, PIX * 5, OFFSET + PIX, PIX * 13),
    Box(PIX * 11, OFFSET, PIX * 3, PIX * 13, OFFSET + PIX, PIX * 5),
    Box(PIX * 11, OFFSET, PIX * 11, PIX * 13, OFFSET + PIX, PIX * 13),
    Box(PIX * 7, OFFSET, PIX * 7, PIX * 9, OFFSET + PIX, PIX * 9)
  );

  private val facingBoxes by lazy {
    Direction.values().associateWith { dir ->
      boxes.map { it.toMul16().rotate(dir).toDiv16() }
    }
  }

  override fun asString() = name.lowercase(Locale.ROOT)

  fun size() = boxes.size
  fun boxesFor(facing: Direction) =
    facingBoxes[facing] ?: facingBoxes[Direction.DOWN]!!

  fun defaultStacks(): DefaultedList<ItemStack> =
    DefaultedList.ofSize(size(), ItemStack.EMPTY)
}
