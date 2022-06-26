package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable;
import pw.switchcraft.plethora.util.Vec2d;

public class LineLoop extends Polygon implements Scalable {
	private float scale = 1;

	public LineLoop(int id, int parent) {
		super(id, parent, ObjectRegistry.LINE_LOOP_2D);
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

	@Override
	public void writeInitial(PacketByteBuf buf) {
		super.writeInitial(buf);
		buf.writeFloat(scale);
	}

	@Override
	public void readInitial(PacketByteBuf buf) {
		super.readInitial(buf);
		scale = buf.readFloat();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(CanvasClient canvas, MatrixStack matrices) {
		if (points.size() < 2) return;

		setupFlat();
		RenderSystem.lineWidth(scale);

		int red = getRed(), green = getGreen(), blue = getBlue(), alpha = getAlpha();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();

		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		buffer.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.LINES);

		for (Vec2d point : points) {
			buffer.vertex(matrix, (float) point.x(), (float) point.y(), 0).color(red, green, blue, alpha).normal(normal, 0, 1, 0).next();
		}

		// No LINE_LOOP anymore, so close the loop manually
		Vec2d first = points.get(0);
		buffer.vertex(matrix, (float) first.x(), (float) first.y(), 0).color(red, green, blue, alpha).normal(normal, 0, 1, 0).next();

		BufferRenderer.drawWithShader(buffer.end());
		RenderSystem.lineWidth(1);
	}
}
