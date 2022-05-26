package pw.switchcraft.plethora.gameplay.neural;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import pw.switchcraft.plethora.gameplay.BaseItem;
import pw.switchcraft.plethora.gameplay.registry.Registration;

import static dan200.computercraft.shared.Registry.ModItems.POCKET_COMPUTER_ADVANCED;
import static pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler.COMPUTER_ID;
import static pw.switchcraft.plethora.gameplay.neural.NeuralHelpers.BACK;

public class NeuralInterfaceRecipe extends ShapedRecipe {
    public NeuralInterfaceRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
        super(id, group, width, height, input, output);
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack output = getOutput().copy(); // The .copy() here is VERY important. Whoops.

        if (output.isEmpty()) {
            // If the input pocket computer has NBT, vanilla will set the output to ItemStack.EMPTY, which is not what
            // we want, so set it back to a neural interface. This will interfere with data packs that change the output
            // of this recipe, but I'm not especially concerned about that right now.
            output = new ItemStack(Registration.ModItems.NEURAL_INTERFACE, 1);
        }

        // Get the old pocket computer
        ItemStack old = getStackInRowAndColumn(inv, 1, 1);
        int id = POCKET_COMPUTER_ADVANCED.getComputerID(old);
        String label = POCKET_COMPUTER_ADVANCED.getLabel(old);

        // Copy across key properties
        NbtCompound nbt = BaseItem.getNbt(output);
        if (label != null && !label.isEmpty()) output.setCustomName(Text.of(label));
        if (id >= 0) nbt.putInt(COMPUTER_ID, id);

        // Forge/1.12.2 Plethora does not check if the source pocket computer has an upgrade, but I feel like it would
        // kinda suck to lose your pocket's ender modem when upgrading it to a neural interface, so let's grab that too.
        IPocketUpgrade upgrade = ItemPocketComputer.getUpgrade(old);
        if (upgrade != null) {
            // Check if the neural will actually accept the item before trying to add it. Add to the BACK slot (2)
            ItemStack upgradeStack = upgrade.getCraftingItem();
            if (NeuralHelpers.isItemValid(BACK, upgradeStack)) {
                NeuralInterfaceInventory neuralInv = new NeuralInterfaceInventory(output);
                neuralInv.setStack(BACK, upgradeStack);

                // Write the new inventory to our output's NBT
                Inventories.writeNbt(nbt, neuralInv.getOwnStacks());
            }
        }

        return output;
    }

    private ItemStack getStackInRowAndColumn(CraftingInventory inv, int row, int column) {
        return row >= 0 && row < inv.getWidth() && column >= 0 && column <= inv.getHeight()
            ? inv.getStack(row + column * inv.getWidth())
            : ItemStack.EMPTY;
    }
}
