package pw.switchcraft.plethora.gameplay.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

public class FramebufferGlasses {
    public static final FramebufferGlasses ITEM_2D = new FramebufferGlasses();

    private Framebuffer fbo;

    public Framebuffer getFbo() {
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        int width = window.getFramebufferWidth(), height = window.getFramebufferHeight();

        if (fbo == null) {
            fbo = new SimpleFramebuffer(width, height, true, false);
        }

        if (fbo.textureWidth != width || fbo.textureHeight != height) {
            fbo.resize(width, height, false);
        }

        return fbo;
    }
}
