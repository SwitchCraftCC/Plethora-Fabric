package io.sc3.plethora.gameplay.neural;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import io.sc3.plethora.gameplay.BaseItem;

import javax.annotation.Nonnull;
import java.util.Optional;

public class NeuralConnectorItem extends BaseItem {
    public NeuralConnectorItem(Settings settings) {
        super("neuralConnector", settings);
    }

    // true on success, false on failure
    private boolean useHandler(World world, PlayerEntity player, LivingEntity entity, ItemStack stack) {
        Optional<Pair<SlotReference, ItemStack>> optNeuralSlotPair = NeuralHelpers.getSlot(entity);
        if (optNeuralSlotPair.isEmpty()) return false;
        Pair<SlotReference, ItemStack> slotPair = optNeuralSlotPair.get();

        if (!world.isClient) {
            ServerComputer computer = NeuralComputerHandler.getServer(slotPair.getRight(), entity, slotPair.getLeft());
            computer.turnOn();

            // We prevent the neural connector from opening when they're already using an interface. This
            // prevents the GUI becoming unusable when one gets in a right-click loop due to a broken program.
            if (!(player.currentScreenHandler instanceof NeuralInterfaceScreenHandler)) {
                player.openHandledScreen(new NeuralInterfaceScreenFactory(entity, slotPair.getRight(), computer));
            }
        }

        return true;
    }

    @Override
    @Nonnull
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (useHandler(world, player, player, stack)) {
            return TypedActionResult.success(stack);
        } else {
            return TypedActionResult.fail(stack);
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (useHandler(user.getWorld(), user, entity, stack)) {
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

    // TODO: remove these TODOs?
    // TODO: itemInteractionForEntity
    // TODO: onEntityInteract
}
