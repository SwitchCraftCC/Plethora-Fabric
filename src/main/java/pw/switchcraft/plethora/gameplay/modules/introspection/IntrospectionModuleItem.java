package pw.switchcraft.plethora.gameplay.modules.introspection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.modules.BindableModuleItem;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.INTROSPECTION_M;

public class IntrospectionModuleItem extends BindableModuleItem {
    public static final Text CONTAINER_TEXT = Text.translatable("container.enderchest");

    public IntrospectionModuleItem(Settings settings) {
        super("introspection", settings);
    }

    @Override
    public TypedActionResult<ItemStack> onBindableModuleUse(World world, PlayerEntity player, Hand hand) {
        // Allow the player to open their ender chest by using the introspection module

        EnderChestInventory inv = player.getEnderChestInventory();
        if (inv != null) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInv, player2) ->
                GenericContainerScreenHandler.createGeneric9x3(syncId, playerInv, inv), CONTAINER_TEXT));
            player.incrementStat(Stats.OPEN_ENDERCHEST);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return INTROSPECTION_M;
    }
}
