package pw.switchcraft.plethora.api.module;

import dan200.computercraft.api.client.TransformedModel;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3f;

import javax.annotation.Nonnull;

/**
 * A basic module handler.
 */
public class BasicModuleHandler extends AbstractModuleHandler {
	private final Identifier id;
	private final Item item;

	public BasicModuleHandler(Identifier id, Item item) {
		this.id = id;
		this.item = item;
	}

	@Nonnull
	@Override
	public Identifier getModule() {
		return id;
	}

	@Nonnull
	@Override
	public TransformedModel getModel(float delta) {
		return TransformedModel.of(
			item.getDefaultStack(),
			new AffineTransformation(null, Vec3f.POSITIVE_Y.getDegreesQuaternion(delta), null, null)
		);
	}

	//	@Nonnull
//	@Override
//	@SideOnly(Side.CLIENT)
//	public Pair<IBakedModel, Matrix4f> getModel(float delta) {
//		Matrix4f matrix = new Matrix4f();
//		matrix.setIdentity();
//		matrix.setRotation(new AxisAngle4f(0f, 1f, 0f, delta));
//
//		IBakedModel model = this.model;
//		if (model == null) {
//			model = this.model = RenderHelpers.getMesher().getItemModel(new ItemStack(item));
//		}
//
//		return Pair.of(model, matrix);
//	}
}
