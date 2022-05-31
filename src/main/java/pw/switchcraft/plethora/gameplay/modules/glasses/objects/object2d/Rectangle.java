package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import com.google.common.base.Objects;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Matrix4f;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ColourableObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry;
import pw.switchcraft.plethora.util.ByteBufUtils;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.core.ContextHelpers.safeFromTarget;

public class Rectangle extends ColourableObject implements Positionable2d {
	private Vec2d position = Vec2d.ZERO;
	private float width;
	private float height;

	public Rectangle(int id, int parent) {
		super(id, parent, ObjectRegistry.RECTANGLE_2D);
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

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setSize(float width, float height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			setDirty();
		}
	}

	@Override
	public void writeInitial(PacketByteBuf buf) {
		super.writeInitial(buf);
		ByteBufUtils.writeVec2d(buf, position);
		buf.writeFloat(width);
		buf.writeFloat(height);
	}

	@Override
	public void readInitial(PacketByteBuf buf) {
		super.readInitial(buf);
		position = ByteBufUtils.readVec2d(buf);
		width = buf.readFloat();
		height = buf.readFloat();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(CanvasClient canvas, MatrixStack matrices) {
		setupFlat();

		float minX = (float) position.x(), minY = (float) position.y();
		float maxX = minX + width, maxY = minY + height;
		int red = getRed(), green = getGreen(), blue = getBlue(), alpha = getAlpha();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex(matrix, minX, minY, 0.0f).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, minX, maxY, 0.0f).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, maxX, maxY, 0.0f).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, maxX, minY, 0.0f).color(red, green, blue, alpha).next();
		buffer.end();

		BufferRenderer.draw(buffer);
	}

	public static final BasicMethod<Rectangle> GET_SIZE = BasicMethod.of(
		"getSize", "function():number, number -- Get the size of this rectangle.",
		Rectangle::getSize, false
	);
	public static FutureMethodResult getSize(IUnbakedContext<Rectangle> unbaked, IArguments args) throws LuaException {
		Rectangle rect = safeFromTarget(unbaked);
		return FutureMethodResult.result(rect.getWidth(), rect.getHeight());
	}

	public static final BasicMethod<Rectangle> SET_SIZE = BasicMethod.of(
		"setSize", "function(number, number) -- Set the size of this rectangle.",
		Rectangle::setSize, false
	);
	public static FutureMethodResult setSize(IUnbakedContext<Rectangle> unbaked, IArguments args) throws LuaException {
		float width = (float) args.getDouble(0), height = (float) args.getDouble(1);
		safeFromTarget(unbaked).setSize(width, height);
		return FutureMethodResult.empty();
	}
}
