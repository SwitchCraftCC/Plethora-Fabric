package io.sc3.plethora.gameplay.modules.keyboard

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity
import dan200.computercraft.shared.computer.core.ServerComputer
import dan200.computercraft.shared.computer.core.ServerContext
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory
import io.sc3.plethora.gameplay.registry.Registration.ModScreens.KEYBOARD_HANDLER_TYPE
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand

class KeyboardScreenHandlerFactory(
  private val computer: ServerComputer,
  private val name: Text,
  private val item: KeyboardModuleItem,
  private val hand: Hand
) : NamedScreenHandlerFactory {
  override fun getDisplayName() = name

  override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler =
    ComputerMenuWithoutInventory(KEYBOARD_HANDLER_TYPE, syncId, inv, ::canUse, computer, computer.family)

  private fun canUse(player: PlayerEntity): Boolean {
    if (!player.isAlive) return false

    val stack = player.getStackInHand(hand)
    if (stack.item != item) return false

    val world = player.world as? ServerWorld ?: return false
    val registry = ServerContext.get(world.server).registry()

    // Ensure the computer still exists
    val newComputer = registry.get(computer.instanceID)
    if (newComputer != computer) return false

    // Find the computer in the world, and perform the same canUseKeyboard check on its (new?) block entity. This allows
    // for binding keyboards to moving turtles
    val blockEntity = world.getBlockEntity(newComputer.position) as? AbstractComputerBlockEntity ?: return false
    return KeyboardModuleItem.canUseKeyboard(world, player, blockEntity.pos, blockEntity)
  }
}
