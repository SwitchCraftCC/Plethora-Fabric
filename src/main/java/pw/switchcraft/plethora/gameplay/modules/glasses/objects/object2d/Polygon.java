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
import java.util.ArrayList;

public class Polygon extends ColourableObject implements MultiPointResizable2d {
	protected final ArrayList<Vec2d> points = new ArrayList<>();

	protected Polygon(int id, int parent, byte type) {
		super(id, parent, type);
	}

	public Polygon(int id, int parent) {
		super(id, parent, ObjectRegistry.POLYGON_2D);
	}

	@Nonnull
	@Override
	public Vec2d getPoint(int idx) {
		return points.get(idx);
	}

	@Override
	public void setVertex(int idx, @Nonnull Vec2d point) {
		if (!Objects.equal(points.get(idx), point)) {
			points.set(idx, point);
			setDirty();
		}
	}

	@Override
	public int getVertices() {
		return points.size();
	}

	@Override
	public void removePoint(int idx) {
		points.remove(idx);
		setDirty();
	}

	@Override
	public void addPoint(int idx, @Nonnull Vec2d point) {
		if (idx == points.size()) {
			points.add(point);
		} else {
			points.add(idx, point);
		}

		setDirty();
	}

	@Override
	public void writeInitial(@Nonnull PacketByteBuf buf) {
		super.writeInitial(buf);
		buf.writeByte(points.size());

		for (Vec2d point : points) ByteBufUtils.writeVec2d(buf, point);
	}

	@Override
	public void readInitial(@Nonnull PacketByteBuf buf) {
		super.readInitial(buf);
		int count = buf.readUnsignedByte();
		points.ensureCapacity(count);

		if (points.size() > count) points.subList(count, points.size()).clear();

		for (int i = 0; i < count; i++) {
			Vec2d point = ByteBufUtils.readVec2d(buf);
			if (i < points.size()) {
				points.set(i, point);
			} else {
				points.add(point);
			}
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(@Nonnull CanvasClient canvas, @Nonnull MatrixStack matrices) {
		if (points.size() < 3) return;

		setupFlat();

		int size = points.size();
		Vec2d a = points.get(0);

		int red = getRed(), green = getGreen(), blue = getBlue(), alpha = getAlpha();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

		for (int i = 1; i < size - 1; i++) {
			Vec2d b = points.get(i), c = points.get(i + 1);
			buffer.vertex(matrix, (float) a.x(), (float) a.y(), 0).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, (float) b.x(), (float) b.y(), 0).color(red, green, blue, alpha).next();
			buffer.vertex(matrix, (float) c.x(), (float) c.y(), 0).color(red, green, blue, alpha).next();
		}

		BufferRenderer.drawWithShader(buffer.end());
	}
}
