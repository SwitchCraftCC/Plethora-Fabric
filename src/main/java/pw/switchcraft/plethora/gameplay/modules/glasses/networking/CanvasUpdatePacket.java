package pw.switchcraft.plethora.gameplay.modules.glasses.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectRegistry;

import java.util.ArrayList;
import java.util.List;

public class CanvasUpdatePacket {
    private int canvasId;
    private List<BaseObject> changed;
    private int[] removed;

    public CanvasUpdatePacket(int canvasId, List<BaseObject> changed, int[] removed) {
        this.canvasId = canvasId;
        this.changed = changed;
        this.removed = removed;
    }

    public CanvasUpdatePacket() {}

    public void fromBytes(PacketByteBuf buf) {
        canvasId = buf.readInt();

        int changedLength = buf.readInt();
        List<BaseObject> changed = this.changed = new ArrayList<>(changedLength);
        for (int i = 0; i < changedLength; i++) {
            changed.add(ObjectRegistry.read(buf));
        }

        // We sort on ID in order to guarantee parents are loaded before their children
        changed.sort(BaseObject.SORTING_ORDER);

        int removedLength = buf.readInt();
        int[] removed = this.removed = new int[removedLength];
        for (int i = 0; i < removedLength; i++) {
            removed[i] = buf.readInt();
        }
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(canvasId);

        buf.writeInt(changed.size());
        for (BaseObject object : changed) {
            ObjectRegistry.write(buf, object);
        }

        buf.writeInt(removed.length);
        for (int id : removed) {
            buf.writeInt(id);
        }
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = PacketByteBufs.create();
        toBytes(buf);
        return buf;
    }

    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                                 PacketSender responseSender) {
        CanvasUpdatePacket packet = new CanvasUpdatePacket();
        packet.fromBytes(buf);

        CanvasClient canvas = CanvasHandler.getClient(packet.canvasId);
        if (canvas == null) return;

        synchronized (canvas) {
            for (BaseObject obj : packet.changed) canvas.updateObject(obj);
            for (int id : packet.removed) canvas.remove(id);
        }
    }
}
