package pw.switchcraft.plethora.gameplay.modules.glasses.objects;

import net.minecraft.network.PacketByteBuf;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.ObjectGroup2d;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.object2d.Rectangle;

public final class ObjectRegistry {
	public static final byte RECTANGLE_2D = 0;
	// public static final byte LINE_2D = 1;
	// public static final byte DOT_2D = 2;
	// public static final byte TEXT_2D = 3;
	// public static final byte TRIANGLE_2D = 4;
	// public static final byte POLYGON_2D = 5;
	// public static final byte LINE_LOOP_2D = 6;
	// public static final byte ITEM_2D = 7;
	public static final byte GROUP_2D = 8;

	// public static final byte ORIGIN_3D = 9;
	// public static final byte FRAME_3D = 10;
	// public static final byte BOX_3D = 11;
	// public static final byte ITEM_3D = 12;
	// public static final byte LINE_3D = 13;

	private static final BaseObject.Factory[] FACTORIES = new BaseObject.Factory[]{
		Rectangle::new,
		null, // Line::new,
		null, // Dot::new,
		null, // Text::new,
		null, // Triangle::new,
		null, // Polygon::new,
		null, // LineLoop::new,
		null, // Item2D::new,
		ObjectGroup2d::new,

		null, // ObjectRoot3D::new,
		null, // ObjectFrame::new,
		null, // Box::new,
		null, // Item3D::new,
		null, // Line3D::new
	};

	private ObjectRegistry() {
	}

	public static BaseObject create(int id, int parent, byte type) {
		if (type < 0 || type >= FACTORIES.length) throw new IllegalStateException("Unknown type " + type);

		BaseObject.Factory factory = FACTORIES[type];
		if (factory == null) throw new IllegalStateException("No factory for type " + type);

		BaseObject object = factory.create(id, parent);
		if (object.type() != type) {
			throw new IllegalStateException("Created object of type " + object.type() + ", expected " + type);
		}

		return object;
	}

	public static BaseObject read(PacketByteBuf buf) {
		int id = buf.readVarInt();
		int parent = buf.readVarInt();
		byte type = buf.readByte();

		BaseObject object = ObjectRegistry.create(id, parent, type);
		object.readInitial(buf);
		return object;
	}

	public static void write(PacketByteBuf buf, BaseObject object) {
		buf.writeVarInt(object.id());
		buf.writeVarInt(object.parent());
		buf.writeByte(object.type());
		object.writeInitial(buf);
	}
}
