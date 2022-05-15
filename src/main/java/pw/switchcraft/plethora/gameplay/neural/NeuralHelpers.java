package pw.switchcraft.plethora.gameplay.neural;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.PocketUpgrades;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;
import pw.switchcraft.plethora.gameplay.registry.Registration;
import pw.switchcraft.plethora.util.Config;

import javax.annotation.Nonnull;
import java.util.Optional;

public class NeuralHelpers {
    public static final int MODULE_SIZE = 5;
    public static final int PERIPHERAL_SIZE = 5;

    public static final int INV_SIZE = MODULE_SIZE + PERIPHERAL_SIZE;

    public static final int BACK = 2;

    public static Optional<Pair<SlotReference, ItemStack>> getSlot(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity)
            .flatMap(c -> c.getEquipped(Registration.ModItems.NEURAL_INTERFACE)
                .stream().findFirst());
    }

    public static Optional<ItemStack> getStack(LivingEntity entity) {
        var slotPair = getSlot(entity);
        return slotPair.map(Pair::getRight);
    }

    public static boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (slot < PERIPHERAL_SIZE) {
            // Check if the item stack is a peripheral by checking if it is included in the allowed peripherals list and
            // is registered as a pocket computer upgrade. This may be made more extensible in the future.
            Identifier id = Registry.ITEM.getId(stack.getItem());
            IPocketUpgrade upgrade = PocketUpgrades.get(stack);
            return upgrade != null && Config.NeuralInterface.peripheralItemIds.contains(id.toString());
        } else {
            return stack.getItem() instanceof ModuleItem;
        }
    }
}
