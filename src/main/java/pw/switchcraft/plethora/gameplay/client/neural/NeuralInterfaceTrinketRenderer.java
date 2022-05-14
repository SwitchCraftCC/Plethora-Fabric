package pw.switchcraft.plethora.gameplay.client.neural;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

public class NeuralInterfaceTrinketRenderer implements TrinketRenderer {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/models/neural_interface.png");

    private NeuralInterfaceModel model;

    public NeuralInterfaceTrinketRenderer() {
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
                       float headPitch) {
        NeuralInterfaceModel model = getModel();

        model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        model.animateModel(entity, limbAngle, limbDistance, tickDelta);
        TrinketRenderer.followBodyRotations(entity, model);

        VertexConsumer consumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        model.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    }

    @Environment(EnvType.CLIENT)
    private NeuralInterfaceModel getModel() {
        if (model == null) {
            model = new NeuralInterfaceModel();
        }

        return model;
    }
}
