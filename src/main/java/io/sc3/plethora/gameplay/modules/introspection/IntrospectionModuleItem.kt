package io.sc3.plethora.gameplay.modules.introspection

import io.sc3.plethora.gameplay.modules.BindableModuleItem
import io.sc3.plethora.gameplay.registry.PlethoraModules.INTROSPECTION_M
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class IntrospectionModuleItem(settings: Settings) : BindableModuleItem("introspection", settings) {
  override fun getModule(): Identifier = INTROSPECTION_M

  override fun onBindableModuleUse(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
    // Allow the player to open their ender chest by using the introspection module
    val inv = player.enderChestInventory
    if (inv != null) {
      player.openHandledScreen(SimpleNamedScreenHandlerFactory({ syncId, playerInv, _ ->
        GenericContainerScreenHandler.createGeneric9x3(syncId, playerInv, inv)
      }, CONTAINER_TEXT))
      player.incrementStat(Stats.OPEN_ENDERCHEST)
    }

    return TypedActionResult.success(player.getStackInHand(hand))
  }

  companion object {
    val CONTAINER_TEXT: Text = translatable("container.enderchest")
  }
}
