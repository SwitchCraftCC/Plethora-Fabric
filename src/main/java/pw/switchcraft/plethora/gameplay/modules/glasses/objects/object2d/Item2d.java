package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ItemObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable;
import pw.switchcraft.plethora.util.ByteBufUtils;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Item2d extends BaseObject implements Scalable, ItemObject, Positionable2d {
	private float scale;
	private Vec2d position = Vec2d.ZERO;

	private int damage;
	private Item item;
	private ItemStack stack;

	public Item2d(int id, int parent) {
		super(id, parent, ObjectRegistry.ITEM_2D);
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		if (this.scale != scale) {
			this.scale = scale;
			setDirty();
		}
	}

	@Nonnull
	@Override
	public Vec2d getPosition() {
		return position;
	}

	@Override
	public void setPosition(@Nonnull Vec2d position) {
		if (!this.position.equals(position)) {
			this.position = position;
			setDirty();
		}
	}

	@Override
	@Nonnull
	public Item getItem() {
		return item;
	}

	@Override
	public void setItem(@Nonnull Item item) {
		if (this.item != item) {
			this.item = item;
			stack = null;
			setDirty();
		}
	}

	@Override
	public void writeInitial(@Nonnull PacketByteBuf buf) {
		ByteBufUtils.writeVec2d(buf, position);
		buf.writeFloat(scale);
		buf.writeString(Registry.ITEM.getId(item).toString());
		buf.writeInt(damage);
	}

	@Override
	public void readInitial(@Nonnull PacketByteBuf buf) {
		position = ByteBufUtils.readVec2d(buf);
		scale = buf.readFloat();

		Identifier name = new Identifier(buf.readString());
		item = Registry.ITEM.get(name);

		damage = buf.readInt();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(@Nonnull CanvasClient canvas, @Nonnull MatrixStack matrices,
                   @Nullable VertexConsumerProvider consumers) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ItemRenderer itemRenderer = mc.getItemRenderer();
		ClientPlayerEntity player = mc.player;
		if (player == null) return;

		matrices.push();

		matrices.translate(position.x(), position.y(), 0.0f);
		matrices.scale(scale, scale, 1);

		// RenderSystem.enableRescaleNormal();
		// RenderSystem.enableAlpha();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

		if (stack == null) stack = new ItemStack(item);

		MatrixStack renderStack = RenderSystem.getModelViewStack();
		renderStack.push();
		// renderStack.loadIdentity();
		renderStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
		RenderSystem.applyModelViewMatrix();

		itemRenderer.zOffset = 200.0f;
		itemRenderer.renderInGuiWithOverrides(player, stack, 0, 0, 0);
		itemRenderer.zOffset = 0;

		renderStack.pop();
		RenderSystem.applyModelViewMatrix();

		matrices.pop();
	}
}
