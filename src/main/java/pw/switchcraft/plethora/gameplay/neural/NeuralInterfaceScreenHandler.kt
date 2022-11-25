package pw.switchcraft.plethora.gameplay.neural

import dan200.computercraft.shared.computer.core.ComputerFamily.ADVANCED
import dan200.computercraft.shared.computer.core.ServerComputer
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu
import dan200.computercraft.shared.network.container.ComputerContainerData
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import pw.switchcraft.plethora.gameplay.client.gui.NeuralInterfaceScreen.Companion.BORDER
import pw.switchcraft.plethora.gameplay.neural.NeuralHelpers.INV_SIZE
import pw.switchcraft.plethora.gameplay.registry.Registration.ModScreens.NEURAL_INTERFACE_HANDLER_TYPE
import pw.switchcraft.plethora.util.Vec2i
import java.util.function.Predicate

class NeuralInterfaceScreenHandler private constructor(
  syncId: Int,
  playerInv: PlayerInventory,
  private val neuralInv: Inventory,
  canUse: Predicate<PlayerEntity>,
  computer: ServerComputer?,
  data: ComputerContainerData?,
) : AbstractComputerMenu(
  NEURAL_INTERFACE_HANDLER_TYPE, syncId, canUse, ADVANCED, computer, data
) {
  val peripheralSlots: List<Slot>
  val moduleSlots: List<Slot>

  init {
    peripheralSlots = addSlots(neuralInv, 0, NeuralHelpers.PERIPHERAL_SIZE)
    moduleSlots = addSlots(neuralInv, NeuralHelpers.PERIPHERAL_SIZE, NeuralHelpers.MODULE_SIZE)

    for (y in 0 until 3) {
      for (x in 0 until 9) {
        addSlot(Slot(playerInv, x + y * 9 + 9, MAIN_START_X + x * S, START_Y + 1 + y * S))
      }
    }

    for (x in 0 until 9) {
      addSlot(Slot(playerInv, x, MAIN_START_X + x * S, START_Y + 54 + 5))
    }

    neuralInv.onOpen(playerInv.player)
  }

  private fun addSlots(inv: Inventory, offset: Int, length: Int): List<Slot> =
    (0 until length).map { NeuralSlot(inv, offset + it, 0, 0) }.onEach(::addSlot)

  override fun close(player: PlayerEntity) {
    super.close(player)
    // Ensure the inventory is saved
    neuralInv.onClose(player)
  }

  override fun quickMove(player: PlayerEntity, index: Int): ItemStack {
    val slot = slots[index]
    if (!slot.hasStack()) return ItemStack.EMPTY

    val existing = slot.stack.copy()
    val result = existing.copy()

    if (index < INV_SIZE) {
      // One of our neural slots, insert into the player's inventory
      if (!insertItem(existing, INV_SIZE, INV_SIZE + 36, true)) return ItemStack.EMPTY
    } else {
      // One of the player's inventory slots (hopefully!), insert into the neural inventory
      if (!insertItem(existing, 0, INV_SIZE, false)) return ItemStack.EMPTY
    }

    if (existing.isEmpty) {
      slot.stack = ItemStack.EMPTY
    } else {
      slot.markDirty()
    }

    if (existing.count == result.count) return ItemStack.EMPTY

    slot.onTakeItem(player, existing)
    return result
  }

  companion object {
    const val START_Y = 134

    private const val MAIN_START_X = BORDER + SIDEBAR_WIDTH
    const val NEURAL_START_X = 185 + SIDEBAR_WIDTH

    // Slot size
    const val S = 18

    // Pixel coordinates for the slots
    val slotPositions = arrayOf(
      Vec2i(NEURAL_START_X + 1 + S, START_Y + 1 + 2 * S),
      Vec2i(NEURAL_START_X + 1 + S, START_Y + 1),

      // Center
      Vec2i(NEURAL_START_X + 1 + S, START_Y + 1 + S),

      Vec2i(NEURAL_START_X + 1 + 2 * S, START_Y + 1 + S),
      Vec2i(NEURAL_START_X + 1, START_Y + 1 + S)
    )

    val swapBtn = Vec2i(NEURAL_START_X + 1 + 2 * S, START_Y + 1 + 2 * S)

    @JvmStatic
    fun of(syncId: Int, playerInv: PlayerInventory, parent: LivingEntity, stack: ItemStack, computer: ServerComputer) =
      NeuralInterfaceScreenHandler(
        syncId, playerInv, NeuralInterfaceInventory(stack),
        { it.isAlive && parent.isAlive && NeuralHelpers.getStack(parent).map { it == stack }.orElse(false) },
        computer, null
      )

    @JvmStatic
    fun of(syncId: Int, playerInv: PlayerInventory, data: ComputerContainerData) =
      NeuralInterfaceScreenHandler(syncId, playerInv, SimpleInventory(NeuralHelpers.INV_SIZE), { true }, null, data)
  }
}
