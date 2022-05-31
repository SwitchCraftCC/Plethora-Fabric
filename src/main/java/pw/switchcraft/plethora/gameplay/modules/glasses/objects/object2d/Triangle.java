package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import com.google.common.base.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Matrix4f;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry;
import pw.switchcraft.plethora.util.ByteBufUtils;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class Triangle extends ColourableObject implements MultiPoint2d {
	private final Vec2d[] points = new Vec2d[3];

	public Triangle(int id, int parent) {
		super(id, parent, ObjectRegistry.TRIANGLE_2D);
		Arrays.fill(points, Vec2d.ZERO);
	}

	@Nonnull
	@Override
	public Vec2d getPoint(int idx) {
		return points[idx];
	}

	@Override
	public void setVertex(int idx, @Nonnull Vec2d point) {
		if (!Objects.equal(points[idx], point)) {
			points[idx] = point;
			setDirty();
		}
	}

	@Override
	public int getVertices() {
		return 3;
	}

	@Override
	public void writeInitial(PacketByteBuf buf) {
		super.writeInitial(buf);
		for (Vec2d point : points) ByteBufUtils.writeVec2d(buf, point);
	}

	@Override
	public void readInitial(PacketByteBuf buf) {
		super.readInitial(buf);
		for (int i = 0; i < points.length; i++) points[i] = ByteBufUtils.readVec2d(buf);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(CanvasClient canvas, MatrixStack matrices) {
		setupFlat();

		int red = getRed(), green = getGreen(), blue = getBlue(), alpha = getAlpha();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
		buffer.vertex(matrix, (float) points[0].x(), (float) points[0].y(), 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, (float) points[1].x(), (float) points[1].y(), 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, (float) points[2].x(), (float) points[2].y(), 0).color(red, green, blue, alpha).next();
		buffer.end();

		BufferRenderer.draw(buffer);
	}
}
