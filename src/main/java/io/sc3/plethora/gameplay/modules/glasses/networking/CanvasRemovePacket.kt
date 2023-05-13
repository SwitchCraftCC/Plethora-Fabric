package io.sc3.plethora.gameplay.modules.glasses.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler;

public class CanvasRemovePacket {
    private int canvasId;

    public CanvasRemovePacket(int canvasId) {
        this.canvasId = canvasId;
    }

    public CanvasRemovePacket() {}

    public void fromBytes(PacketByteBuf buf) {
        canvasId = buf.readInt();
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeInt(canvasId);
    }

    public PacketByteBuf toBytes() {
        PacketByteBuf buf = PacketByteBufs.create();
        toBytes(buf);
        return buf;
    }

    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                                 PacketSender responseSender) {
        CanvasRemovePacket packet = new CanvasRemovePacket();
        packet.fromBytes(buf);

        CanvasClient canvas = CanvasHandler.getClient(packet.canvasId);
        if (canvas == null) return;

        CanvasHandler.removeClient(canvas);
    }
}
