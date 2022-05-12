package pw.switchcraft.plethora.gameplay.client.entity;

import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.util.RenderLayerHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import pw.switchcraft.plethora.gameplay.modules.laser.LaserEntity;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

public class LaserRenderer extends EntityRenderer<LaserEntity> {
    private static final float SCALE = 0.05625f;

    private static final ManagedCoreShader shader = ShaderEffectManager.getInstance()
        .manageCoreShader(new Identifier(MOD_ID, "laser"));

    private static final RenderLayer LAYER = shader.getRenderLayer(RenderLayerHelper.copy(
        RenderLayer.getTranslucent(),
        "laser_render_layer",
        b -> b.cull(RenderPhase.DISABLE_CULLING)
    ));

    public LaserRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(LaserEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.prevYaw + (entity.getYaw() - entity.prevYaw) * tickDelta - 90.0f));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(entity.prevPitch + (entity.getPitch() - entity.prevPitch) * tickDelta));

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrices.scale(SCALE, SCALE, SCALE);
        matrices.translate(-4.0f, 0.0f, 0.0f);

        VertexConsumer consumer = vertexConsumers.getBuffer(LAYER);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        for (int i = 0; i < 2; i++) {
            matrix4f.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));

            vertex(consumer, matrix4f, -9, -2   , 0, 0, 1, 0.0f);
            vertex(consumer, matrix4f, +9, -2   , 0, 1, 1, 0.0f);
            vertex(consumer, matrix4f, +9, -1.5f, 0, 1, 0, 0.25f);
            vertex(consumer, matrix4f, -9, -1.5f, 0, 0, 0, 0.25f);

            vertex(consumer, matrix4f, -9, -1.5f, 0, 0, 1, 0.25f);
            vertex(consumer, matrix4f, +9, -1.5f, 0, 1, 1, 0.25f);
            vertex(consumer, matrix4f, +9, +0   , 0, 1, 0, 0.8f);
            vertex(consumer, matrix4f, -9, +0   , 0, 0, 1, 0.8f);

            vertex(consumer, matrix4f, -9, +0   , 0, 0, 1, 0.8f);
            vertex(consumer, matrix4f, +9, +0   , 0, 1, 1, 0.8f);
            vertex(consumer, matrix4f, +9, +1.5f, 0, 1, 0, 0.25f);
            vertex(consumer, matrix4f, -9, +1.5f, 0, 0, 0, 0.25f);

            vertex(consumer, matrix4f, -9, +1.5f, 0, 0, 1, 0.25f);
            vertex(consumer, matrix4f, +9, +1.5f, 0, 1, 1, 0.25f);
            vertex(consumer, matrix4f, +9, +2   , 0, 1, 0, 0.0f);
            vertex(consumer, matrix4f, -9, +2   , 0, 0, 0, 0.0f);
        }

        matrices.pop();
    }

    @Override
    public Identifier getTexture(LaserEntity entity) {
        return null;
    }

    private void vertex(VertexConsumer consumer, Matrix4f matrix4f, float x, float y, float z, float u, float v, float alpha) {
        consumer
            .vertex(matrix4f, x, y, z)
            .color(1, 0, 0, alpha)
            .texture(u, v)
            .light(1)
            .normal(0.0f, 0.0f, 1.0f)
            .next();
    }
}
