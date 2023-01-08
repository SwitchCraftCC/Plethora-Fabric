package io.sc3.plethora.gameplay.modules.keyboard

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity
import dan200.computercraft.shared.computer.blocks.CommandComputerBlockEntity
import dan200.computercraft.shared.network.container.ComputerContainerData
import dan200.computercraft.shared.platform.PlatformHelper
import io.sc3.plethora.api.method.IAttachable
import io.sc3.plethora.api.method.IContextBuilder
import io.sc3.plethora.api.module.IModuleAccess
import io.sc3.plethora.gameplay.modules.ModuleItem
import io.sc3.plethora.gameplay.registry.PlethoraModules
import io.sc3.plethora.mixin.computercraft.AbstractComputerBlockEntityAccessor
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult.CONSUME
import net.minecraft.util.ActionResult.PASS
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.max

private const val REACH_RANGE = 32.0

class KeyboardModuleItem(settings: Settings) : ModuleItem("keyboard", settings) {
  override fun getModule(): Identifier = PlethoraModules.KEYBOARD_M

  override fun useOnBlock(ctx: ItemUsageContext): ActionResult {
    if (ctx.world.isClient) return CONSUME

    val world = ctx.world as? ServerWorld ?: return CONSUME
    val player = ctx.player as? ServerPlayerEntity ?: return CONSUME

    return useOnComputer(world, player, ctx.blockPos, ctx.hand)
  }

  override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
    val stack = player.getStackInHand(hand)
    if (world.isClient) return TypedActionResult(PASS, stack)
    return TypedActionResult(useInAir(world, player, hand), stack)
  }

  private fun useInAir(world: World, player: PlayerEntity, hand: Hand): ActionResult {
    if (world !is ServerWorld || player !is ServerPlayerEntity) {
      return CONSUME
    }

    // Long-distance reach: ray-cast up to 32 blocks away
    val hit = player.raycast(REACH_RANGE, 0.0f, false) as? BlockHitResult ?: return PASS

    return useOnComputer(world, player, hit.blockPos, hand)
  }

  private fun useOnComputer(world: ServerWorld, player: ServerPlayerEntity, pos: BlockPos, hand: Hand): ActionResult {
    val blockEntity = world.getBlockEntity(pos) as? AbstractComputerBlockEntity ?: return PASS
    val computer = blockEntity.serverComputer ?: return PASS
    val stack = player.getStackInHand(hand)

    // Check if the user has permission to use a keyboard here
    if (!canUseKeyboard(world, player, pos, blockEntity)) return PASS

    ComputerContainerData(computer, stack).open(
      player,
      KeyboardScreenHandlerFactory(computer, stack.name, this, hand)
    )

    return CONSUME // Don't play an animation
  }

  override fun getAdditionalContext(stack: ItemStack, access: IModuleAccess, builder: IContextBuilder) {
    super.getAdditionalContext(stack, access, builder)

    val player = access.owner as? ServerPlayerEntity ?: return
    builder.addAttachable(object : IAttachable {
      override fun attach() {
        ServerKeyListener.add(player, access)
      }

      override fun detach() {
        ServerKeyListener.remove(player, access)
      }
    })
  }

  companion object {
    @JvmStatic
    fun canUseKeyboard(world: ServerWorld, player: PlayerEntity, pos: BlockPos,
                       blockEntity: AbstractComputerBlockEntity): Boolean {
      // canPlayerModifyAt: check the player can modify the world (spawn protection and ClaimKit should work here)
      // isUsable: check the player is alive and is within range. Command computers also check that the player has
      //           permission to use the computer.
      return world.canPlayerModifyAt(player, pos) && isComputerUsableWithRange(player, blockEntity, REACH_RANGE)
    }

    @JvmStatic
    private fun isComputerUsableWithRange(player: PlayerEntity, blockEntity: AbstractComputerBlockEntity,
                                          maxRange: Double): Boolean {
      // Perform the same checks as CC's isUsable, but with a custom interaction range and without checking if the
      // block entity was removed (so we can use keyboards with moving turtles)
      // BlockEntityHelpers.isUsable: the base usable check
      // AbstractComputerBlockEntity: lootable locked check, BEH.isUsable
      // CommandComputerBlockEntity:  isCommandUsable, AbstractComputerBlockEntity.isUsable
      if (blockEntity is CommandComputerBlockEntity && !CommandComputerBlockEntity.isCommandUsable(player)) {
        return false
      }

      val lockCode = (blockEntity as AbstractComputerBlockEntityAccessor).lockCode
      val range = max(maxRange, PlatformHelper.get().getReachDistance(player))

      return LootableContainerBlockEntity.checkUnlocked(player, lockCode, blockEntity.displayName)
        && player.isAlive
        && player.entityWorld == blockEntity.world
        && player.squaredDistanceTo(Vec3d.ofCenter(blockEntity.pos)) <= range * range
    }
  }
}
