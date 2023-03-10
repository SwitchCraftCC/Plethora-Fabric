package io.sc3.plethora.gameplay.modules.glasses.objects.object3d

import com.mojang.blaze3d.systems.RenderSystem
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ItemObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.ITEM_3D
import io.sc3.plethora.gameplay.modules.glasses.objects.Scalable
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.DirtyingProperty
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager.MAX_LIGHT_COORDINATE
import net.minecraft.client.render.OverlayTexture.DEFAULT_UV
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

class Item3d(
  id: Int,
  parent: Int
) : BaseObject(id, parent, ITEM_3D), Scalable, Positionable3d, DepthTestable, ItemObject, Rotatable3d {
  override var position by DirtyingProperty(Vec3d.ZERO!!)
  override var rotation: Vec3d? by DirtyingProperty(Vec3d.ZERO)
  override var hasDepthTest by DirtyingProperty(true)
  override var scale by DirtyingProperty(1f)

  private var stack: ItemStack? = null
  override var item by DirtyingProperty(Items.STONE!!) { _, _, _ -> stack = null }
  // TODO: Damage?

  override fun readInitial(buf: PacketByteBuf) {
    position = ByteBufUtils.readVec3d(buf)
    rotation = ByteBufUtils.readOptVec3d(buf)
    scale = buf.readFloat()

    val name = Identifier(buf.readString())
    item = Registries.ITEM[name]

    hasDepthTest = buf.readBoolean()
  }

  override fun writeInitial(buf: PacketByteBuf) {
    ByteBufUtils.writeVec3d(buf, position)
    ByteBufUtils.writeOptVec3d(buf, rotation)
    buf.writeFloat(scale)
    buf.writeString(Registries.ITEM.getId(item).toString())
    buf.writeBoolean(hasDepthTest)
  }

  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    val mc = MinecraftClient.getInstance()
    val itemRenderer = mc.itemRenderer

    matrices.push()

    matrices.translate(position.x, position.y, position.z)
    matrices.scale(scale, scale, scale)
    applyRotation(matrices, true)

    val buffer = Tessellator.getInstance().buffer
    val immediate = VertexConsumerProvider.immediate(buffer)

    if (hasDepthTest) {
      RenderSystem.enableDepthTest()
    } else {
      RenderSystem.disableDepthTest()
    }

    val stack = stack ?: ItemStack(item).also { stack = it }
    itemRenderer.renderItem(stack, ModelTransformationMode.NONE, MAX_LIGHT_COORDINATE, DEFAULT_UV, matrices,
      immediate, mc.world, 0)

    immediate.draw()

    matrices.pop()
  }
}
