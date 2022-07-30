package pw.switchcraft.plethora.gameplay.client.block;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.client.entity.LaserRenderer;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorType;

import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock.BOX_EXPAND;
import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock.FACING;

public class ManipulatorOutlineRenderer {
    private static final RenderLayer GLOW_LAYER = LaserRenderer.LAYER;

    private static float ticks;
    private static final float GLOW_OFFSET = 0.005f;
    private static final double GLOW_PERIOD = 20.0;

    public static boolean onBlockOutline(WorldRenderContext worldCtx, WorldRenderContext.BlockOutlineContext ctx) {
        ticks += worldCtx.tickDelta();

        World world = ctx.entity().world;
        BlockPos pos = ctx.blockPos();

        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof ManipulatorBlock manipulator)) return true;

        HitResult result = MinecraftClient.getInstance().crosshairTarget;
        if (result == null || result.getType() != HitResult.Type.BLOCK) return true;

        Direction facing = state.get(FACING);
        boolean down = facing == Direction.DOWN;

        Vec3d hit = result.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
        ManipulatorType type = manipulator.getType();

        for (Box box : type.boxesFor(facing)) {
            Box expandBox = box.expand(BOX_EXPAND);
            if (expandBox.contains(hit)) {
                Box rb = expandBox.offset(pos).offset(worldCtx.camera().getPos().negate());
                MatrixStack matrixStack = worldCtx.matrixStack();

                // Glowing square
                if (down) {
                    // TODO: Because Boxes, this doesn't work for any direction except DOWN. Fix later
                    VertexConsumer glow = worldCtx.consumers().getBuffer(GLOW_LAYER);
                    Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

                    float alpha = 0.4f + ((float) Math.sin(ticks / GLOW_PERIOD) * 0.1f);
                    vertex(glow, matrix4f, rb.minX, rb.minY + GLOW_OFFSET, rb.minZ, 0, 1, alpha);
                    vertex(glow, matrix4f, rb.maxX, rb.minY + GLOW_OFFSET, rb.minZ, 1, 1, alpha);
                    vertex(glow, matrix4f, rb.maxX, rb.minY + GLOW_OFFSET, rb.maxZ, 1, 0, alpha);
                    vertex(glow, matrix4f, rb.minX, rb.minY + GLOW_OFFSET, rb.maxZ, 0, 0, alpha);
                }

                // Box outline
                VertexConsumer outline = worldCtx.consumers().getBuffer(RenderLayer.getLines());

                WorldRenderer.drawBox(matrixStack, outline, rb, 0.0f, 0.0f, 0.0f, 0.4f);

                return false;
            }
        }

        return true;
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, double x, double y, double z, float u, float v,
                               float alpha) {
        consumer // POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
            .vertex(matrix4f, (float) x, (float) y, (float) z)
            .color(1, 1, 1, alpha)
            .texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(1)
            .normal(0.0f, 0.0f, 1.0f)
            .next();
    }
}
