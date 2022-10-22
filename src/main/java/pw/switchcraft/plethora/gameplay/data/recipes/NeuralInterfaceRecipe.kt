package pw.switchcraft.plethora.gameplay.data.recipes

import dan200.computercraft.shared.Registry.ModItems.POCKET_COMPUTER_ADVANCED
import dan200.computercraft.shared.Registry.ModItems.WIRED_MODEM
import dan200.computercraft.shared.pocket.items.ItemPocketComputer
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient.*
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import pw.switchcraft.library.recipe.BetterSpecialRecipe
import pw.switchcraft.plethora.gameplay.neural.NeuralComputerHandler
import pw.switchcraft.plethora.gameplay.neural.NeuralHelpers
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceInventory
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems.NEURAL_INTERFACE

class NeuralInterfaceRecipe(id: Identifier) : BetterSpecialRecipe(id) {
  override val ingredients = listOf(
    EMPTY,                EMPTY,                             fromTag(GOLD_INGOTS),
    fromTag(IRON_INGOTS), ofItems(POCKET_COMPUTER_ADVANCED), fromTag(REDSTONE_DUSTS),
    EMPTY,                fromTag(GOLD_INGOTS),              ofItems(WIRED_MODEM)
  )

  override val outputItem = ItemStack(NEURAL_INTERFACE)

  override fun craft(inv: CraftingInventory): ItemStack {
    val output = ItemStack(outputItem.item)

    // Get the old pocket computer
    val old = stackAtPos(inv, 1, 1)
    val id = POCKET_COMPUTER_ADVANCED.getComputerID(old)
    val label = POCKET_COMPUTER_ADVANCED.getLabel(old)

    // Copy across key properties
    val nbt = output.orCreateNbt
    if (!label.isNullOrEmpty()) output.setCustomName(Text.of(label))
    if (id >= 0) nbt.putInt(NeuralComputerHandler.COMPUTER_ID, id)

    // Forge/1.12.2 Plethora does not check if the source pocket computer has an upgrade, but I feel like it would kinda
    // suck to lose your pocket's ender modem when upgrading it to a neural interface, so let's grab that too.
    val upgrade = ItemPocketComputer.getUpgrade(old)
    if (upgrade != null) {
      // Check if the neural will actually accept the item before trying to add it. Add to the BACK slot (2)
      val upgradeStack = upgrade.craftingItem
      if (NeuralHelpers.isItemValid(NeuralHelpers.BACK, upgradeStack)) {
        val neuralInv = NeuralInterfaceInventory(output)
        neuralInv.setStack(NeuralHelpers.BACK, upgradeStack)

        // Write the new inventory to our output's NBT
        Inventories.writeNbt(nbt, neuralInv.ownStacks)
      }
    }

    return output
  }

  private fun stackAtPos(inv: CraftingInventory, row: Int, column: Int) =
    if (row >= 0 && row < inv.width && column >= 0 && column <= inv.height) {
      inv.getStack(row + column * inv.width)
    } else {
      ItemStack.EMPTY
    }

  override fun isIgnoredInRecipeBook() = false
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer { NeuralInterfaceRecipe(it) }
  }
}
