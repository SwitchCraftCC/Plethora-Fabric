package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object3d

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager.MAX_LIGHT_COORDINATE
import net.minecraft.client.render.OverlayTexture.DEFAULT_UV
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation.Mode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ItemObject
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry.ITEM_3D
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable
import pw.switchcraft.plethora.util.ByteBufUtils
import pw.switchcraft.plethora.util.DirtyingProperty

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
    item = Registry.ITEM[name]

    hasDepthTest = buf.readBoolean()
  }

  override fun writeInitial(buf: PacketByteBuf) {
    ByteBufUtils.writeVec3d(buf, position)
    ByteBufUtils.writeOptVec3d(buf, rotation)
    buf.writeFloat(scale)
    buf.writeString(Registry.ITEM.getId(item).toString())
    buf.writeBoolean(hasDepthTest)
  }

  override fun draw(canvas: CanvasClient, matrices: MatrixStack, consumers: VertexConsumerProvider?) {
    val mc = MinecraftClient.getInstance()
    val itemRenderer = mc.itemRenderer

    matrices.push()

    matrices.translate(position.x, position.y, position.z)
    matrices.scale(scale, scale, scale)
    applyRotation(matrices)

    RenderSystem.enableTexture()

    if (hasDepthTest) {
      RenderSystem.enableDepthTest()
    } else {
      RenderSystem.disableDepthTest()
    }

    val stack = stack ?: ItemStack(item).also { stack = it }
    itemRenderer.renderItem(stack, Mode.NONE, MAX_LIGHT_COORDINATE, DEFAULT_UV, matrices, consumers, 0)

    matrices.pop()
  }
}
