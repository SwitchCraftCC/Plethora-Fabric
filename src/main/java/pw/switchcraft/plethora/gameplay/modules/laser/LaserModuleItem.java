package pw.switchcraft.plethora.gameplay.modules.laser;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;

import static pw.switchcraft.plethora.util.Config.Laser.maximumPotency;
import static pw.switchcraft.plethora.util.Config.Laser.minimumPotency;

public class LaserModuleItem extends ModuleItem {
    private static final int MAX_TICKS = 72000;
    private static final int USE_TICKS = 30;

    /**
     * We multiply the gaussian by this number.
     * This is the change in velocity for each axis after normalisation.
     *
     * @see net.minecraft.entity.projectile.ProjectileEntity#setVelocity(double, double, double, float, float)
     */
    private static final float LASER_MAX_SPREAD = (float) (0.1 / 0.007499999832361937);

    public LaserModuleItem(Settings settings) {
        super("laser", settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MAX_TICKS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // TODO: Check module blacklist here

        player.setCurrentHand(hand);
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity player, int remainingUseTicks) {
        if (world.isClient) return;
        // TODO: Check module blacklist here

        // Get the number of ticks the laser has been used for
        // We use a float so we'll have to cast it later anyway
        float ticks = MAX_TICKS - remainingUseTicks;
        if (ticks > USE_TICKS) ticks = USE_TICKS;
        if (ticks < 0) ticks = 0;

        double inaccuracy = (USE_TICKS - ticks) / USE_TICKS * LASER_MAX_SPREAD;
        double potency = (ticks / USE_TICKS) * (maximumPotency - minimumPotency) + minimumPotency;

        world.spawnEntity(new LaserEntity(world, player, (float) inaccuracy, (float) potency));
    }
}
