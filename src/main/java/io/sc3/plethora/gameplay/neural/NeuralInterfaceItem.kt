package io.sc3.plethora.gameplay.neural

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.api.filesystem.Mount
import dan200.computercraft.api.media.IMedia
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ComputerFamily.ADVANCED
import dan200.computercraft.shared.computer.items.IComputerItem
import dan200.computercraft.shared.config.Config.computerSpaceLimit
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import io.sc3.library.Tooltips.addDescLines
import io.sc3.plethora.Plethora.modId
import io.sc3.plethora.gameplay.neural.NeuralComputerHandler.COMPUTER_ID
import io.sc3.plethora.gameplay.neural.NeuralComputerHandler.DIRTY
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting.GRAY
import net.minecraft.world.World
import javax.annotation.Nonnull

class NeuralInterfaceItem(settings: Settings?) : TrinketItem(settings), IComputerItem, IMedia {
  override fun getTranslationKey() = "item.$modId.neuralInterface"

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    super.appendTooltip(stack, world, tooltip, context)
    addDescLines(tooltip, getTranslationKey(stack))

    if (context.isAdvanced) {
      val nbt = stack.nbt
      if (nbt != null && nbt.contains(COMPUTER_ID)) {
        tooltip.add(translatable("gui.plethora.tooltip.computer_id", getComputerID(stack))
          .formatted(GRAY))
      }
    }
  }

  override fun getComputerID(@Nonnull stack: ItemStack): Int {
    val nbt = stack.nbt
    return if (nbt != null && nbt.contains(COMPUTER_ID)) nbt.getInt(COMPUTER_ID) else -1
  }

  override fun getLabel(@Nonnull stack: ItemStack): String? =
    if (stack.hasCustomName()) stack.name.string else null

  override fun setLabel(@Nonnull stack: ItemStack, label: String?): Boolean {
    if (label == null) {
      stack.removeCustomName()
    } else {
      stack.setCustomName(Text.of(label))
    }
    return true
  }

  override fun createDataMount(@Nonnull stack: ItemStack, @Nonnull world: ServerWorld): Mount? {
    val id = getComputerID(stack)
    return if (id < 0) {
      null
    } else {
      ComputerCraftAPI.createSaveDirMount(world.server, "computer/$id", computerSpaceLimit.toLong())
    }
  }

  override fun getFamily() = ADVANCED

  override fun withFamily(stack: ItemStack, @Nonnull family: ComputerFamily) = stack

  override fun tick(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
    onUpdate(stack, slot, entity, true)
  }

  companion object {
    private fun onUpdate(stack: ItemStack, slot: SlotReference?, player: LivingEntity, forceActive: Boolean) {
      if (!player.entityWorld.isClient) {
        val nbt = stack.orCreateNbt

        // Fetch computer
        val neural = if (forceActive) {
          NeuralComputerHandler.getServer(stack, player, slot!!).also { it.keepAlive() }
        } else {
          NeuralComputerHandler.tryGetServer(stack, player) ?: return
        }

        var dirty = false

        // Sync computer ID
        val newId = neural.id
        if (!nbt.contains(COMPUTER_ID) || nbt.getInt(COMPUTER_ID) != newId) {
          nbt.putInt(COMPUTER_ID, newId)
          dirty = true
        }

        // Sync Label
        val newLabel = neural.label
        val label = if (stack.hasCustomName()) stack.name.string else null
        if (newLabel != label) {
          if (newLabel == null || newLabel.isEmpty()) {
            stack.removeCustomName()
          } else {
            stack.setCustomName(Text.of(newLabel))
          }
          dirty = true
        }

        // Sync and update peripherals
        val dirtyStatus = nbt.getShort(DIRTY)
        if (dirtyStatus.toInt() != 0) {
          nbt.putShort(DIRTY, 0.toShort())
          dirty = true
        }

        if (neural.update(player, stack, dirtyStatus.toInt())) {
          dirty = true
        }

        if (dirty && slot != null) {
          slot.inventory().markDirty()
        }
      }
    }
  }
}
