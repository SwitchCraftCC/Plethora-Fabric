package pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d;

import com.google.common.base.Objects;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry;
import pw.switchcraft.plethora.util.ByteBufUtils;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;

public class ObjectGroup2d extends BaseObject implements ObjectGroup.Group2d, Positionable2d {
	private Vec2d position = Vec2d.ZERO;

	public ObjectGroup2d(int id, int parent) {
		super(id, parent, ObjectRegistry.GROUP_2D);
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
	public void writeInitial(PacketByteBuf buf) {
		ByteBufUtils.writeVec2d(buf, position);
	}

	@Override
	public void readInitial(PacketByteBuf buf) {
		position = ByteBufUtils.readVec2d(buf);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(CanvasClient canvas, MatrixStack matrices) {
		IntSet children = canvas.getChildren(id());
		if (children == null) return;

		matrices.push();
		matrices.translate(position.x(), position.y(), 0);

		canvas.drawChildren(children.iterator(), matrices);

		matrices.pop();
	}
}
