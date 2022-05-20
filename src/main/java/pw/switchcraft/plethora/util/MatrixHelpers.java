package pw.switchcraft.plethora.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

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
			result.loadIdentity();
			result.multiplyByTranslation(0.5f, 0.5f, 0.5f);
			result.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-y));
			result.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-x));
			result.multiplyByTranslation(-0.5f, -0.5f, -0.5f);
			FACINGS[facing.ordinal()] = result;
		}
	}

	public static Matrix4f matrixFor(Direction facing) {
		int index = facing.ordinal();
		return index < FACINGS.length ? FACINGS[index] : IDENTITY;
	}

	public static Box transform(Box box, Matrix4f matrix) {
		return new Box(
			(float) (matrix.a00 * box.minX + matrix.a01 * box.minY + matrix.a02 * box.minZ + matrix.a03),
			(float) (matrix.a10 * box.minX + matrix.a11 * box.minY + matrix.a12 * box.minZ + matrix.a13),
			(float) (matrix.a20 * box.minX + matrix.a21 * box.minY + matrix.a22 * box.minZ + matrix.a23),

			(float) (matrix.a00 * box.maxX + matrix.a01 * box.maxY + matrix.a02 * box.maxZ + matrix.a03),
			(float) (matrix.a10 * box.maxX + matrix.a11 * box.maxY + matrix.a12 * box.maxZ + matrix.a13),
			(float) (matrix.a20 * box.maxX + matrix.a21 * box.maxY + matrix.a22 * box.maxZ + matrix.a23)
		);
	}
}
