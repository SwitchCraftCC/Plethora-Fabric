package io.sc3.plethora.gameplay.modules.glasses

import io.sc3.plethora.api.method.IContextBuilder
import io.sc3.plethora.api.module.IModuleAccess
import io.sc3.plethora.gameplay.modules.ModuleItem
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasServer
import io.sc3.plethora.gameplay.registry.PlethoraModules.GLASSES_M
import io.sc3.plethora.gameplay.registry.PlethoraModules.GLASSES_S
import io.sc3.plethora.util.FakePlayer
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class GlassesModuleItem(settings: Settings) : ModuleItem("glasses", settings) {
  override fun getModule(): Identifier = GLASSES_M

  override fun getAdditionalContext(stack: ItemStack, access: IModuleAccess, builder: IContextBuilder) {
    super.getAdditionalContext(stack, access, builder)

    val owner = access.owner
    if (owner is ServerPlayerEntity && owner !is FakePlayer) {
      val glasses = CanvasServer(access, owner)
      builder.addContext(GLASSES_S, glasses).addAttachable(glasses)
    }
  }
}
