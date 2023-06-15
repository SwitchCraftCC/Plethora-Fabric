package io.sc3.plethora.gameplay.modules.glasses.objects.object2d

import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectGroup.Group2d
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry.GROUP_2D
import io.sc3.plethora.util.ByteBufUtils
import io.sc3.plethora.util.DirtyingProperty
import io.sc3.plethora.util.Vec2d
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.network.PacketByteBuf
import javax.annotation.Nonnull

class ObjectGroup2d(
  id: Int,
  parent: Int
) : BaseObject(id, parent, GROUP_2D), Group2d, Positionable2d {
  override var position by DirtyingProperty(Vec2d.ZERO!!)

  override fun writeInitial(@Nonnull buf: PacketByteBuf) {
    ByteBufUtils.writeVec2d(buf, position)
  }

  override fun readInitial(@Nonnull buf: PacketByteBuf) {
    position = ByteBufUtils.readVec2d(buf)
  }

  @Environment(EnvType.CLIENT)
  override fun draw(canvas: CanvasClient, ctx: DrawContext, consumers: VertexConsumerProvider?) {
    val children = canvas.getChildren(id) ?: return
    val matrices = ctx.matrices
    matrices.push()
    matrices.translate(position.x(), position.y(), 0.0)
    canvas.drawChildren(children.iterator(), ctx, consumers)
    matrices.pop()
  }
}
