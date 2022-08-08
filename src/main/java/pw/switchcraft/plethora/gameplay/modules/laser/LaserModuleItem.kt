package pw.switchcraft.plethora.gameplay.modules.laser;

import dan200.computercraft.api.client.TransformedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import pw.switchcraft.plethora.api.method.IContextBuilder;
import pw.switchcraft.plethora.api.module.IModuleAccess;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.LASER_M;
import static pw.switchcraft.plethora.util.config.Config.Laser.maximumPotency;
import static pw.switchcraft.plethora.util.config.Config.Laser.minimumPotency;

public class LaserModuleItem extends ModuleItem implements IModuleHandler {
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

    @Nonnull
    @Override
    public Identifier getModule() {
        return LASER_M;
    }

    @Override
    public void getAdditionalContext(@Nonnull ItemStack stack, @Nonnull IModuleAccess access, @Nonnull IContextBuilder builder) {
        // TODO: this is very important!
    }

    @Nonnull
    @Override
    public TransformedModel getModel(float delta) {
        // Flip the laser so it points forwards on turtles
        return TransformedModel.of(
            getDefaultStack(),
            new AffineTransformation(null, Vec3f.POSITIVE_Y.getDegreesQuaternion((delta + 180) % 360), null, null)
        );
    }
}
