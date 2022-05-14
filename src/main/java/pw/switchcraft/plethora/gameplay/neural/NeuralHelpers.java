package pw.switchcraft.plethora.gameplay.neural;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;
import pw.switchcraft.plethora.gameplay.registry.Registration;
import pw.switchcraft.plethora.util.Config;
import pw.switchcraft.plethora.util.TinySlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NeuralHelpers {
    public static final EquipmentSlot ARMOR_SLOT = EquipmentSlot.HEAD;

    public static final int MODULE_SIZE = 5;
    public static final int PERIPHERAL_SIZE = 5;

    public static final int INV_SIZE = MODULE_SIZE + PERIPHERAL_SIZE;

    public static final int BACK = 2;

    @Nullable
    public static TinySlot getSlot(LivingEntity entity) {
        ItemStack stack = entity.getEquippedStack(ARMOR_SLOT);

        if (!stack.isEmpty() && stack.getItem() == Registration.ModItems.NEURAL_INTERFACE) {
            return entity instanceof PlayerEntity player
                ? new TinySlot.InventorySlot(stack, player.getInventory())
                : new TinySlot(stack);
        }

        // TODO: Baubles or something similar? Custom neural slot?

        return null;
    }

    @Nonnull
    public static ItemStack getStack(LivingEntity entity) {
        TinySlot slot = getSlot(entity);
        return slot == null ? ItemStack.EMPTY : slot.getStack();
    }

    public static boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (slot < PERIPHERAL_SIZE) {
            // Check if the item stack is a peripheral by checking if it is included in the allowed peripherals list
            // TODO: This is absolutely NOT comprehensive, but with the loss of capabilities, I'm not sure how else this
            //       could be done. I toyed around with checking if it's a BlockItem -> BlockEntity -> IPeripheralTile,
            //       but that's *really* silly.
            // TODO: Later thoughts: Since you need to implement peripheral integration anyway, and the only supported
            //       vanilla peripherals are speakers and wireless modems, I think we can just do away with this check
            //       and instead check if we have an integration handler for the peripheral.
            Identifier id = Registry.ITEM.getId(stack.getItem());
            return Config.NeuralInterface.peripheralItemIds.contains(id.toString());
        } else {
            // TODO: Check if item has module handler capability
            return stack.getItem() instanceof ModuleItem;
        }
    }
}
