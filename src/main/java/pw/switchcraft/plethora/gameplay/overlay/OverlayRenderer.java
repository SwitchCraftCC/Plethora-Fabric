package pw.switchcraft.plethora.gameplay.overlay;

import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import pw.switchcraft.plethora.gameplay.registry.Registration;

import static net.fabricmc.api.EnvType.CLIENT;

public class OverlayRenderer {
    private static float ticks = 0;

    @Environment(CLIENT)
    public static void renderOverlay(
        MinecraftClient client,
        MatrixStack matrices,
        float tickDelta,
        Camera camera
    ) {
        ticks += tickDelta;
        if (ticks > Math.PI * 2 * 1000) ticks = 0;

        ClientPlayerEntity player = client.player;
        if (player == null) return;

        // Prevent rendering an overlay twice if it is in both hands
        ItemStack renderScanner = null, renderSensor = null;

        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (renderScanner == null && item == Registration.ModItems.SCANNER_MODULE) renderScanner = stack;
            else if (renderSensor == null && item == Registration.ModItems.SENSOR_MODULE) renderSensor = stack;
            // TODO: Chat recorder?
        }

        if (renderScanner != null) ScannerOverlayRenderer.render(player, renderScanner, matrices, ticks, tickDelta, camera);
        if (renderSensor != null) SensorOverlayRenderer.render(player, renderSensor, matrices, ticks, camera);
    }
}
