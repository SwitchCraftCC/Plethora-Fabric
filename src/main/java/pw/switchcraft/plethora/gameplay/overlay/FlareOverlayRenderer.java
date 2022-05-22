package pw.switchcraft.plethora.gameplay.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.awt.*;
import java.util.Map;

import static com.mojang.blaze3d.platform.GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA;
import static com.mojang.blaze3d.platform.GlStateManager.DstFactor.ZERO;
import static com.mojang.blaze3d.platform.GlStateManager.SrcFactor.ONE;
import static com.mojang.blaze3d.platform.GlStateManager.SrcFactor.SRC_ALPHA;
import static net.minecraft.client.render.VertexFormat.DrawMode.QUADS;
import static net.minecraft.client.render.VertexFormats.POSITION_TEXTURE;
import static pw.switchcraft.plethora.Plethora.MOD_ID;

public class FlareOverlayRenderer {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/misc/flare.png");

    public record FlareColor(float r, float g, float b, float offset) {}

    public static void initFlareRenderer(MatrixStack matrices, Camera camera) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);
        RenderSystem.enableTexture();

        matrices.push();

        matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    public static void uninitFlareRenderer(MatrixStack matrices) {
        matrices.pop();

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    public static void renderFlare(MatrixStack matrices, Camera camera,
                                   float ticks, double x, double y, double z, FlareColor color, float size) {
        matrices.push();

        // Setup the view
        matrices.translate(x, y, z);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

        // The size is function of ticks and the id: ensures slightly different sizes
        size *= 0.2f + MathHelper.sin(ticks / 100.0f + color.offset) / 16.0f;

        // Prepare to render
        Tessellator tessellator = Tessellator.getInstance();

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        // Inner highlight
        RenderSystem.setShaderColor(color.r, color.g, color.b, 0.5f);
        renderQuad(tessellator, matrix4f, size);

        // Outer aura
        RenderSystem.setShaderColor(color.r, color.g, color.b, 0.2f);
        renderQuad(tessellator, matrix4f, size * 2);

        matrices.pop();
    }

    private static void renderQuad(Tessellator tessellator, Matrix4f matrix4f, float size) {
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(QUADS, POSITION_TEXTURE);

        buffer.vertex(matrix4f, -size, -size, 0).texture(0, 1).next();
        buffer.vertex(matrix4f, -size, +size, 0).texture(1, 1).next();
        buffer.vertex(matrix4f, +size, +size, 0).texture(1, 0).next();
        buffer.vertex(matrix4f, +size, -size, 0).texture(0, 0).next();

        tessellator.draw();
    }

    public static float getOffsetFromId(int id) {
        // Generate an offset based off the hash code
        return (float) (id % (Math.PI * 2));
    }

    public static FlareColor getFlareColorById(Map<String, String> colourConfig, Identifier id) {
        String idString = id.toString();

        // Generate an offset based off the hash code
        int hashCode = id.hashCode();
        float offset = getOffsetFromId(hashCode);

        FlareColor color;

        if (colourConfig.containsKey(idString)) {
            // Get the colour from the config
            Color raw = Color.decode(colourConfig.get(idString));
            color = new FlareColor(raw.getRed() / 255.0f, raw.getGreen() / 255.0f, raw.getBlue() / 255.0f, offset);
        } else {
            // Choose a colour from the hash code
            // this isn't very fancy but it generally works
            Color raw = new Color(Color.HSBtoRGB(
                MathHelper.sin(offset) / 2.0f + 0.5f,
                MathHelper.cos(offset) / 2.0f + 0.5f,
                1.0f
            ));

            color = new FlareColor(raw.getRed() / 255.0f, raw.getGreen() / 255.0f, raw.getBlue() / 255.0f, offset);
        }

        return color;
    }
}
