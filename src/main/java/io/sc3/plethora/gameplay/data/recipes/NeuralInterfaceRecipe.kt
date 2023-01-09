package io.sc3.plethora.gameplay.data.recipes

import com.google.gson.JsonObject
import dan200.computercraft.shared.computer.items.IComputerItem
import dan200.computercraft.shared.computer.recipe.ComputerConvertRecipe
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import dan200.computercraft.shared.util.RecipeUtil
import io.sc3.plethora.gameplay.neural.NeuralComputerHandler
import io.sc3.plethora.gameplay.neural.NeuralHelpers
import io.sc3.plethora.gameplay.neural.NeuralInterfaceInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.collection.DefaultedList

class NeuralInterfaceRecipe(
  id: Identifier,
  group: String?,
  category: CraftingRecipeCategory,
  width: Int,
  height: Int,
  ingredients: DefaultedList<Ingredient>,
  result: ItemStack,
) : ComputerConvertRecipe(id, group, category, width, height, ingredients, result) {
  override fun convert(item: IComputerItem, old: ItemStack): ItemStack {
    val id = item.getComputerID(old)
    val label = item.getLabel(old)

    // Copy across key properties
    val nbt = output.orCreateNbt
    if (!label.isNullOrEmpty()) output.setCustomName(Text.of(label))
    if (id >= 0) nbt.putInt(NeuralComputerHandler.COMPUTER_ID, id)

    // Forge/1.12.2 Plethora does not check if the source pocket computer has an upgrade, but I feel like it would kinda
    // suck to lose your pocket's ender modem when upgrading it to a neural interface, so let's grab that too.
    val upgrade = PocketComputerItem.getUpgrade(old)
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

  override fun getSerializer() = Serializer

  object Serializer : RecipeSerializer<NeuralInterfaceRecipe> {
    override fun read(id: Identifier, json: JsonObject): NeuralInterfaceRecipe {
      val group = JsonHelper.getString(json, "group", "")
      val category =
        CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(json, "category", null), CraftingRecipeCategory.MISC)

      val template = RecipeUtil.getTemplate(json)
      val result = outputFromJson(JsonHelper.getObject(json, "result"))

      return NeuralInterfaceRecipe(
        id,
        group,
        category,
        template.width(),
        template.height(),
        template.ingredients(),
        result,
      )
    }

    override fun read(id: Identifier, buf: PacketByteBuf): NeuralInterfaceRecipe {
      val width = buf.readVarInt()
      val height = buf.readVarInt()
      val group = buf.readString()
      val category = buf.readEnumConstant(
        CraftingRecipeCategory::class.java
      )

      val ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY)
      for (i in ingredients.indices) ingredients[i] = Ingredient.fromPacket(buf)

      val result = buf.readItemStack()
      return NeuralInterfaceRecipe(id, group, category, width, height, ingredients, result)
    }

    override fun write(buf: PacketByteBuf, recipe: NeuralInterfaceRecipe) {
      buf.writeVarInt(recipe.width)
      buf.writeVarInt(recipe.height)
      buf.writeString(recipe.group)
      buf.writeEnumConstant(recipe.category)
      for (ingredient in recipe.ingredients) ingredient.write(buf)
      buf.writeItemStack(recipe.output)
    }
  }
}
