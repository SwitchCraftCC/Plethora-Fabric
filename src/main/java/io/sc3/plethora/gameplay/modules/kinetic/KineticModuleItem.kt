package pw.switchcraft.plethora.gameplay.modules.kinetic

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.world.World
import pw.switchcraft.plethora.Plethora.config
import pw.switchcraft.plethora.gameplay.modules.ModuleItem
import pw.switchcraft.plethora.gameplay.registry.PlethoraModules

private const val MAX_TICKS = 72000
private const val USE_TICKS = 30

class KineticModuleItem(settings: Settings) : ModuleItem("kinetic", settings) {
  override fun getModule(): Identifier = PlethoraModules.KINETIC_M

  override fun getUseAction(stack: ItemStack) = UseAction.BOW
  override fun getMaxUseTime(stack: ItemStack) = MAX_TICKS

  override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
    val stack = player.getStackInHand(hand)

    // TODO: Check module blacklist here

    player.setCurrentHand(hand)
    return TypedActionResult.success(stack)
  }

  override fun onStoppedUsing(stack: ItemStack, world: World, player: LivingEntity, remainingUseTicks: Int) {
    if (world.isClient) return
    // TODO: Check module blacklist here

    var ticks = (MAX_TICKS - remainingUseTicks).toFloat()
    if (ticks > USE_TICKS) ticks = USE_TICKS.toFloat()
    if (ticks < 0) ticks = 0f

    KineticMethods.launch(
      player, player.yaw, player.pitch,
      ticks / USE_TICKS * config.kinetic.launchMax
    )
  }
}
