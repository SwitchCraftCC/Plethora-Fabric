package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ItemObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.ITEM_2D
import io.sc3.plethora.gameplay.modules.glasses.objects.Scalable
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.DirtyingProperty
import io.sc3.plethora.util.Vec2d
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class Item2d(
  id: Int,
  parent: Int
) : BaseObject(id, parent, ITEM_2D), Scalable, ItemObject, Positionable2d {
  override var position by DirtyingProperty(Vec2d.ZERO!!)
  override var scale by DirtyingProperty(1f)

  private var stack: ItemStack? = null
  override var item by DirtyingProperty(Items.STONE!!) { _, _, _ -> stack = null }
  // TODO: Damage?

  override fun writeInitial(buf: PacketByteBuf) {
    ByteBufUtils.writeVec2d(buf, position)
    buf.writeFloat(scale)
    buf.writeString(Registries.ITEM.getId(item).toString())
  }

  override fun readInitial(buf: PacketByteBuf) {
    position = ByteBufUtils.readVec2d(buf)
    scale = buf.readFloat()

    val name = Identifier(buf.readString())
    item = Registries.ITEM[name]
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, ctx: DrawContext, consumers: VertexConsumerProvider?) {
    val matrices = ctx.matrices
    matrices.push()

    matrices.translate(position.x(), position.y(), 0.0)
    matrices.scale(scale, scale, 1f)

    // RenderSystem.enableRescaleNormal();
    // RenderSystem.enableAlpha();
    RenderSystem.enableDepthTest()
    RenderSystem.enableBlend()
    RenderSystem.blendFuncSeparate(
      GlStateManager.SrcFactor.SRC_ALPHA,
      GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
      GlStateManager.SrcFactor.ONE,
      GlStateManager.DstFactor.ZERO
    )

    if (stack == null) stack = ItemStack(item)

    val renderStack = RenderSystem.getModelViewStack()
    renderStack.push()
    // renderStack.loadIdentity();
    renderStack.multiplyPositionMatrix(matrices.peek().positionMatrix)
    RenderSystem.applyModelViewMatrix()

    matrices.push()
    // matrices.translate(0.0f, 0.0f, 200.0f)
    ctx.drawItem(stack, 0, 0)
    matrices.pop()

    renderStack.pop()
    RenderSystem.applyModelViewMatrix()

    matrices.pop()
  }
}
