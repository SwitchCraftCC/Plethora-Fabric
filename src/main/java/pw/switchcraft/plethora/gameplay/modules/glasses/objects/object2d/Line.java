package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import com.google.common.base.Objects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable;
import pw.switchcraft.plethora.util.ByteBufUtils;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

public class Line extends ColourableObject implements Scalable, MultiPoint2d {
	private Vec2d start = Vec2d.ZERO;
	private Vec2d end = Vec2d.ZERO;
	private float thickness = 1;

	public Line(int id, int parent) {
		super(id, parent, ObjectRegistry.LINE_2D);
	}

	@Override
	public float getScale() {
		return thickness;
	}

	@Override
	public void setScale(float scale) {
		if (thickness != scale) {
			thickness = scale;
			setDirty();
		}
	}

	@Nonnull
	@Override
	public Vec2d getPoint(int idx) {
		return idx == 0 ? start : end;
	}

	@Override
	public void setVertex(int idx, @Nonnull Vec2d point) {
		if (idx == 0) {
			if (!Objects.equal(start, point)) {
				start = point;
				setDirty();
			}
		} else {
			if (!Objects.equal(end, point)) {
				end = point;
				setDirty();
			}
		}
	}

	@Override
	public int getVertices() {
		return 2;
	}

	@Override
	public void writeInitial(PacketByteBuf buf) {
		super.writeInitial(buf);
		ByteBufUtils.writeVec2d(buf, start);
		ByteBufUtils.writeVec2d(buf, end);
		buf.writeFloat(thickness);
	}

	@Override
	public void readInitial(PacketByteBuf buf) {
		super.readInitial(buf);
		start = ByteBufUtils.readVec2d(buf);
		end = ByteBufUtils.readVec2d(buf);
		thickness = buf.readFloat();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(CanvasClient canvas, MatrixStack matrices) {
		setupFlat();
		RenderSystem.lineWidth(thickness);

		int red = getRed(), green = getGreen(), blue = getBlue(), alpha = getAlpha();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();

		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		buffer.vertex(matrix, (float) start.x(), (float) start.y(), 0).color(red, green, blue, alpha).normal(normal, 0, 1, 0).next();
		buffer.vertex(matrix, (float) end.x(), (float) end.y(), 0).color(red, green, blue, alpha).normal(normal, 0, 1, 0).next();

		BufferRenderer.drawWithShader(buffer.end());
		RenderSystem.lineWidth(1);
	}
}
