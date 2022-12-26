package pw.switchcraft.plethora.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;

public final class MatrixHelpers {
	private MatrixHelpers() {
	}

	private static final Matrix4f IDENTITY = new Matrix4f();

	private static final Matrix4f[] FACINGS;

	static {
		FACINGS = new Matrix4f[Direction.values().length];
		for (Direction facing : Direction.values()) {
			int x = 0, y = 0;
			switch (facing) {
				case DOWN -> {
					x = 0;
					y = 0;
				}
				case UP -> {
					x = 180;
					y = 0;
				}
				case EAST -> {
					x = 90;
					y = 270;
				}
				case WEST -> {
					x = 90;
					y = 90;
				}
				case NORTH -> {
					x = 90;
					y = 180;
				}
				case SOUTH -> {
					x = 90;
					y = 0;
				}
			}

			Matrix4f result = new Matrix4f();
			result.translation(0.5f, 0.5f, 0.5f);
			result.rotateYXZ((float) Math.toRadians(-y), (float) Math.toRadians(-x), 0);
			result.translate(-0.5f, -0.5f, -0.5f);
			FACINGS[facing.ordinal()] = result;
		}
	}

	public static Matrix4f matrixFor(Direction facing) {
		int index = facing.ordinal();
		return index < FACINGS.length ? FACINGS[index] : IDENTITY;
	}

	public static Box transform(Box box, Matrix4f matrix) {
		return new Box(
			(float) (matrix.m00() * box.minX + matrix.m01() * box.minY + matrix.m02() * box.minZ + matrix.m03()),
			(float) (matrix.m10() * box.minX + matrix.m11() * box.minY + matrix.m12() * box.minZ + matrix.m13()),
			(float) (matrix.m20() * box.minX + matrix.m21() * box.minY + matrix.m22() * box.minZ + matrix.m23()),

			(float) (matrix.m00() * box.maxX + matrix.m01() * box.maxY + matrix.m02() * box.maxZ + matrix.m03()),
			(float) (matrix.m10() * box.maxX + matrix.m11() * box.maxY + matrix.m12() * box.maxZ + matrix.m13()),
			(float) (matrix.m20() * box.maxX + matrix.m21() * box.maxY + matrix.m22() * box.maxZ + matrix.m23())
		);
	}
}
