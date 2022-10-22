package pw.switchcraft.plethora.gameplay.modules.laser

import dan200.computercraft.api.client.TransformedModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.AffineTransformation
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World
import pw.switchcraft.plethora.Plethora
import pw.switchcraft.plethora.api.method.IContextBuilder
import pw.switchcraft.plethora.api.module.IModuleAccess
import pw.switchcraft.plethora.api.module.IModuleHandler
import pw.switchcraft.plethora.gameplay.modules.ModuleItem
import pw.switchcraft.plethora.gameplay.registry.PlethoraModules

private const val MAX_TICKS = 72000
private const val USE_TICKS = 30

/**
 * We multiply the gaussian by this number.
 * This is the change in velocity for each axis after normalisation.
 *
 * @see net.minecraft.entity.projectile.ProjectileEntity.setVelocity
 */
private const val LASER_MAX_SPREAD = (0.1 / 0.0075).toFloat()

class LaserModuleItem(settings: Settings) : ModuleItem("laser", settings), IModuleHandler {
  private val cfg by Plethora.config::laser

  override fun getModule(): Identifier = PlethoraModules.LASER_M

  override fun getUseAction(stack: ItemStack): UseAction = UseAction.BOW
  override fun getMaxUseTime(stack: ItemStack) = MAX_TICKS

  override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
    val stack = player.getStackInHand(hand)

    // TODO: Check module blacklist here
    player.setCurrentHand(hand)
    return TypedActionResult(ActionResult.SUCCESS, stack)
  }

  override fun onStoppedUsing(stack: ItemStack, world: World, player: LivingEntity, remainingUseTicks: Int) {
    if (world.isClient) return
    // TODO: Check module blacklist here

    // Get the number of ticks the laser has been used for
    // We use a float, so we'll have to cast it later anyway
    var ticks = (MAX_TICKS - remainingUseTicks).toFloat()
    if (ticks > USE_TICKS) ticks = USE_TICKS.toFloat()
    if (ticks < 0) ticks = 0f

    val inaccuracy = ((USE_TICKS - ticks) / USE_TICKS * LASER_MAX_SPREAD).toDouble()
    val potency = ticks / USE_TICKS * (cfg.maximumPotency - cfg.minimumPotency) + cfg.minimumPotency

    world.spawnEntity(LaserEntity(world, player, inaccuracy.toFloat(), potency.toFloat()))
  }

  override fun getAdditionalContext(stack: ItemStack, access: IModuleAccess, builder: IContextBuilder) {
    // TODO: this is very important!
  }

  override fun getModel(): TransformedModel {
    // Flip the laser so it points forwards on turtles
    return TransformedModel.of(defaultStack, transform)
  }

  companion object {
    private val transform by lazy {
      val matrices = MatrixStack()
      matrices.push()
      matrices.translate(0.5, 0.5, 0.5)
      matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f))
      matrices.translate(-0.5, -0.5, -0.5)

      AffineTransformation(matrices.peek().positionMatrix)
    }
  }
}
