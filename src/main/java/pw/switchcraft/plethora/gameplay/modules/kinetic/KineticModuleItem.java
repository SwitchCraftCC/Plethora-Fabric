package pw.switchcraft.plethora.gameplay.modules.kinetic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;
import static pw.switchcraft.plethora.util.config.Config.Kinetic.launchMax;

public class KineticModuleItem extends ModuleItem {
    public static final Identifier MODULE_ID = new Identifier(MOD_ID, "kinetic");

    private static final int MAX_TICKS = 72000;
    private static final int USE_TICKS = 30;

    public KineticModuleItem(Settings settings) {
        super("kinetic", settings);
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

        float ticks = MAX_TICKS - remainingUseTicks;
        if (ticks > USE_TICKS) ticks = USE_TICKS;
        if (ticks < 0) ticks = 0;

        KineticMethods.launch(player, player.getYaw(), player.getPitch(), (ticks / USE_TICKS) * launchMax);
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return MODULE_ID;
    }
}
