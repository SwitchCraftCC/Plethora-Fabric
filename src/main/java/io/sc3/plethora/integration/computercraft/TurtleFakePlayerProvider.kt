package io.sc3.plethora.integration.computercraft

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.shared.turtle.TurtleUtil
import dan200.computercraft.shared.util.DirectionUtil
import io.sc3.plethora.api.IPlayerOwnable
import io.sc3.plethora.gameplay.PlethoraFakePlayer
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import java.util.*

object TurtleFakePlayerProvider {
  private val registeredPlayers = WeakHashMap<ITurtleAccess, PlethoraFakePlayer>()

  @JvmStatic
  fun getPlayer(entity: ITurtleAccess, ownable: IPlayerOwnable?): PlethoraFakePlayer =
    registeredPlayers.computeIfAbsent(entity) {
      PlethoraFakePlayer(entity.level as ServerWorld, null, ownable?.owningProfile)
    }

  @JvmStatic
  fun load(player: PlethoraFakePlayer, turtle: ITurtleAccess, dir: Direction) {
    player.setWorld(turtle.level as ServerWorld)

    val pos = turtle.position
    player.updatePositionAndAngles(
      pos.x + 0.5 + 0.51 * dir.offsetX,
      pos.y + 0.5 + 0.51 * dir.offsetY,
      pos.z + 0.5 + 0.51 * dir.offsetZ,
      (if (dir.axis !== Direction.Axis.Y) dir else turtle.direction).asRotation(),
      if (dir.axis !== Direction.Axis.Y) 0f else DirectionUtil.toPitchAngle(dir)
    )
    player.setHeadYaw(player.yaw)

    player.isSneaking = false

    val playerInv = player.inventory
    playerInv.selectedSlot = 0

    // Copy primary items into player inventory and empty the rest
    val turtleInv = turtle.inventory
    val size = turtleInv.size()
    val largerSize = playerInv.size()
    playerInv.selectedSlot = turtle.selectedSlot
    for (i in 0 until size) {
      playerInv.setStack(i, turtleInv.getStack(i))
    }
    for (i in size until largerSize) {
      playerInv.setStack(i, ItemStack.EMPTY)
    }

    playerInv.markDirty()

    // Add properties
    val activeStack = player.getStackInHand(Hand.MAIN_HAND)
    if (!activeStack.isEmpty) {
      player.attributes.addTemporaryModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }
  }

  @JvmStatic
  fun unload(player: PlethoraFakePlayer, turtle: ITurtleAccess) {
    val playerInv = player.inventory
    playerInv.selectedSlot = 0

    // Remove properties
    val activeStack = player.getStackInHand(Hand.MAIN_HAND)
    if (!activeStack.isEmpty) {
      player.attributes.removeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }

    // Copy primary items into turtle playerInv and then insert/drop the rest
    val turtleInv = turtle.inventory
    val size = turtleInv.size()
    val largerSize = playerInv.size()
    playerInv.selectedSlot = turtle.selectedSlot
    for (i in 0 until size) {
      turtleInv.setStack(i, playerInv.getStack(i))
      playerInv.setStack(i, ItemStack.EMPTY)
    }

    for (i in size until largerSize) {
      TurtleUtil.storeItemOrDrop(turtle, playerInv.getStack(i))
      playerInv.setStack(i, ItemStack.EMPTY)
    }

    playerInv.markDirty()
    turtleInv.markDirty()
  }
}
