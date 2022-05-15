package pw.switchcraft.plethora.gameplay.neural;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.BaseItem;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceScreenFactory.TargetType;

import javax.annotation.Nonnull;
import java.util.Optional;

public class NeuralConnectorItem extends BaseItem {
    public NeuralConnectorItem(Settings settings) {
        super("neuralConnector", settings);
    }

    @Override
    @Nonnull
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        Optional<Pair<SlotReference, ItemStack>> optNeuralSlotPair = NeuralHelpers.getSlot(player);
        if (optNeuralSlotPair.isEmpty()) return TypedActionResult.fail(stack);
        Pair<SlotReference, ItemStack> slotPair = optNeuralSlotPair.get();

        if (!world.isClient) {
            ServerComputer computer = NeuralComputerHandler.getServer(slotPair.getRight(), player, slotPair.getLeft());
            if (computer != null) computer.turnOn();

            // We prevent the neural connector from opening when they're already using an interface. This
            // prevents the GUI becoming unusable when one gets in a right-click loop due to a broken program.
            if (!(player.currentScreenHandler instanceof NeuralInterfaceScreenHandler)) {
                computer.sendTerminalState(player);
                player.openHandledScreen(new NeuralInterfaceScreenFactory(TargetType.PLAYER, 0));
            }
        }

        return TypedActionResult.success(stack);
    }

    // TODO: itemInteractionForEntity
    // TODO: onEntityInteract
}
