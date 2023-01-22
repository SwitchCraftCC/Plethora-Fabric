package io.sc3.plethora.integration.vanilla.method

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.shared.util.ArgumentHelpers
import io.sc3.plethora.api.IWorldLocation
import io.sc3.plethora.api.method.*
import io.sc3.plethora.api.method.FutureMethodResult.result
import io.sc3.plethora.core.ContextHelpers.fromContext
import io.sc3.plethora.gameplay.registry.PlethoraModules.INTROSPECTION_M
import io.sc3.plethora.util.RangedInventoryWrapper
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory.MAIN_SIZE
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.UseAction
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.util.concurrent.Callable

object RangedInventoryWrapperMethods {
  val CONSUME = BasicMethod.of(
    "consume", "function(slot:number):boolean -- Consume an item from the given slot in the inventory",
    ::consume
  )
  private fun consume(unbaked: IUnbakedContext<RangedInventoryWrapper>, args: IArguments): FutureMethodResult {
    val slot = args.getInt(0)
    ArgumentHelpers.assertBetween(slot, 1, MAIN_SIZE, "Slot out of range (%s)")

    val (_, inventory, player) = unbaked.getInventory()

    // Check if the item is consumable
    return FutureMethodResult.nextTick(Callable {
      val stack = inventory.getStack(slot - 1)
      if (stack.isEmpty) return@Callable result(false)

      val action = stack.useAction
      if (action != UseAction.EAT && action != UseAction.DRINK) {
        return@Callable result(false)
      }

      // Consume the item
      val newStack = stack.finishUsing(player.world, player)
      inventory.setStack(slot - 1, newStack)
      result(true)
    })
  }

  val DROP = BasicMethod.of(
    "drop", "function(slot:number, [limit:number, [direction:string]]):number -- Drop an item on the ground",
    ::drop
  )
  private fun drop(unbaked: IUnbakedContext<RangedInventoryWrapper>, args: IArguments): FutureMethodResult {
    val slot = args.getInt(0)
    ArgumentHelpers.assertBetween(slot, 1, MAIN_SIZE, "Slot out of range (%s)")

    val limit = args.optInt(1, Integer.MAX_VALUE)
    ArgumentHelpers.assertBetween(limit, 1, Integer.MAX_VALUE, "Limit out of range (%s)")

    val direction = args.optString(2, null)

    val (context, inventory, player) = unbaked.getInventory()

    return FutureMethodResult.nextTick(Callable {
      val stack = inventory.getStack(slot - 1)
      if (stack.isEmpty) return@Callable result(0)

      val location = context.getContext(ContextKeys.ORIGIN, IWorldLocation::class.java)

      val dir = when (direction) {
        "forward" -> player.horizontalFacing
        "backward" -> player.horizontalFacing.opposite
        null -> null
        else -> Direction.byName(direction)
          ?: throw IllegalArgumentException("Invalid direction: $direction")
      }

      // Drop the item
      val dropStack = stack.split(limit)
      result(dropItem(location, dropStack, dir))
    })
  }

  private fun dropItem(location: IWorldLocation, stack: ItemStack, dir: Direction?): Int {
    if (stack.isEmpty) return 0

    val world = location.world
    var pos = location.loc
    dir?.let { pos = pos.add(Vec3d(it.unitVector).multiply(0.75)) }

    val entity = ItemEntity(world, pos.x, pos.y, pos.z, stack.copy())
    entity.velocity = Vec3d.ZERO
    entity.setToDefaultPickupDelay()
    (world as? ServerWorld)?.spawnEntity(entity)

    return stack.count
  }

  private data class InventoryData(
    val context: IContext<RangedInventoryWrapper>,
    val inventory: RangedInventoryWrapper,
    val player: PlayerEntity
  )
  private fun IUnbakedContext<RangedInventoryWrapper>.getInventory(): InventoryData {
    val context = bake()
    require(context.modules.hasModule(INTROSPECTION_M)) { "No Introspection module installed" }

    val inventory = context.target
    val player = fromContext(context, PlayerEntity::class.java) ?:
      throw IllegalArgumentException("No player associated with inventory")

    return InventoryData(context, inventory, player)
  }
}
