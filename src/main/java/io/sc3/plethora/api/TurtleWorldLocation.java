package io.sc3.plethora.api;

import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.builder.ToStringBuilder;
import io.sc3.plethora.api.reference.ConstantReference;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A world location for turtles
 */
public class TurtleWorldLocation implements ConstantReference<IWorldLocation>, IWorldLocation {
	private final ITurtleAccess turtle;

	public TurtleWorldLocation(@Nonnull ITurtleAccess turtle) {
		Objects.requireNonNull(turtle, "entity cannot be null");
		this.turtle = turtle;
	}

	@Nonnull
	@Override
	public World getWorld() {
		return turtle.getLevel();
	}

	@Nonnull
	@Override
	public BlockPos getPos() {
		return turtle.getPosition();
	}

	@Nonnull
	@Override
	public Vec3d getLoc() {
		BlockPos pos = turtle.getPosition();
		return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	@Nonnull
	@Override
	public Box getBounds() {
		BlockPos pos = turtle.getPosition();
		return new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	}

	@Nonnull
	@Override
	public IWorldLocation get() {
		return this;
	}

	@Nonnull
	@Override
	public IWorldLocation safeGet() {
		return new WorldLocation(getWorld(), getLoc());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("turtle", turtle)
			.append("world", getWorld())
			.append("loc", getLoc())
			.toString();
	}
}
