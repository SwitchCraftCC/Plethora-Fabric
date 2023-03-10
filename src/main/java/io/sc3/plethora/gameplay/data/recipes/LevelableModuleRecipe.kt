package io.sc3.plethora.gameplay.data.recipes

import io.sc3.plethora.gameplay.modules.LevelableModuleItem
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

abstract class LevelableModuleRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC,
  val module: LevelableModuleItem
) : ShapelessRecipe(
  id, "", category, ItemStack(module), DefaultedList.copyOf(
    Ingredient.EMPTY, // Defaulted item
    ofItems(module), // Module to be upgraded
    ofItems(Items.NETHER_STAR),
    ofItems(Items.NETHERITE_INGOT)
  )
) {
  override fun craft(inv: CraftingInventory, manager: DynamicRegistryManager): ItemStack {
    val output = getOutput(manager)

    for (i in 0 until inv.size()) {
      val stack: ItemStack = inv.getStack(i)
      if (stack.item !is LevelableModuleItem || stack.item !== output.item) {
        continue
      }

      val result = stack.copy()
      result.count = 1

      // Only increment the level if the module is not already at the max - i.e. only if the effective radius is
      // different to before
      val oldLevel = LevelableModuleItem.getLevel(stack)
      val oldRange = LevelableModuleItem.getEffectiveRange(stack)
      val newRange = LevelableModuleItem.getEffectiveRange(stack, oldLevel + 1)
      if (oldRange == newRange) return ItemStack.EMPTY

      // Increment the level by updating the NBT of the result item
      val tag = result.orCreateNbt
      tag.putInt("level", oldLevel + 1)
      return result
    }

    return output.copy()
  }

  override fun isIgnoredInRecipeBook() = false
}
