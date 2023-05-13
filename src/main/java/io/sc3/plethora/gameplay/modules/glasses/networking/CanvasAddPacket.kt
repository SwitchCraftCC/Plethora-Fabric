package io.sc3.plethora.gameplay.modules.glasses.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import io.sc3.plethora.gameplay.modules.glasses.objects.BaseObject;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler;
import io.sc3.plethora.gameplay.modules.glasses.objects.ObjectRegistry;

import java.util.Arrays;

public class CanvasAddPacket {
    private int canvasId;
    private BaseObject[] objects;

    public CanvasAddPacket(int canvasId, BaseObject[] objects) {
        this.canvasId = canvasId;
        this.objects = objects;
    }

    public CanvasAddPacket() {}

    public void fromBytes(PacketByteBuf buf) {
        canvasId = buf.readInt();

        int size = buf.readInt();
        BaseObject[] objects = this.objects = new BaseObject[size];
        for (int i = 0; i < size; i++) {
            objects[i] = ObjectRegistry.read(buf);
        }

        // We sort on ID in order to guarantee parents are loaded before their children
        Arrays.sort(objects, BaseObject.SORTING_ORDER);
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(canvasId);

        buf.writeInt(objects.length);
        for (BaseObject object : objects) {
            ObjectRegistry.write(buf, object);
        }
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = PacketByteBufs.create();
        toBytes(buf);
        return buf;
    }

    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                                 PacketSender responseSender) {
        CanvasAddPacket packet = new CanvasAddPacket();
        packet.fromBytes(buf);

        CanvasClient canvas = new CanvasClient(packet.canvasId);

        for (BaseObject obj : packet.objects) canvas.updateObject(obj);
        CanvasHandler.addClient(canvas);
    }
}
