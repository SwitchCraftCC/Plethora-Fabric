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
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.Scalable;
import pw.switchcraft.plethora.util.ByteBufUtils;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

public class Dot extends ColourableObject implements Positionable2d, Scalable {
	private Vec2d position = Vec2d.ZERO;
	private float scale = 1;

	public Dot(int id, int parent) {
		super(id, parent, ObjectRegistry.DOT_2D);
	}

	@Nonnull
	@Override
	public Vec2d getPosition() {
		return position;
	}

	@Override
	public void setPosition(@Nonnull Vec2d position) {
		if (!Objects.equal(this.position, position)) {
			this.position = position;
			setDirty();
		}
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
		ByteBufUtils.writeVec2d(buf, position);
		buf.writeFloat(scale);
	}

	@Override
	public void readInitial(PacketByteBuf buf) {
		super.readInitial(buf);
		position = ByteBufUtils.readVec2d(buf);
		scale = buf.readFloat();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(CanvasClient canvas, MatrixStack matrices) {
		setupFlat();

		float x = (float) position.x(), y = (float) position.y(), delta = scale / 2;
		int red = getRed(), green = getGreen(), blue = getBlue(), alpha = getAlpha();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

		buffer.vertex(matrix, x - delta, y - delta, 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, x - delta, y + delta, 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, x + delta, y + delta, 0).color(red, green, blue, alpha).next();

		buffer.vertex(matrix, x - delta, y - delta, 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, x + delta, y + delta, 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, x + delta, y - delta, 0).color(red, green, blue, alpha).next();

		BufferRenderer.drawWithShader(buffer.end());
	}
}
