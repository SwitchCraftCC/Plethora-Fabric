package pw.switchcraft.plethora.gameplay.client.neural;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class NeuralInterfaceModel extends BipedEntityModel<LivingEntity> {
    private static TexturedModelData createNormalModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0f);
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData main = modelPartData.addChild("head",
            ModelPartBuilder.create().uv(0, 0).cuboid(-0.1f, -5.0f, -5.1f, 5.0f, 3.0f, 1.0f),
            ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        main.addChild("neural_normal_side",
            ModelPartBuilder.create().uv(5, 0).cuboid(3.9f, -5.0f, -4.1f, 1.0f, 2.0f, 7.0f),
            ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        return TexturedModelData.of(modelData, 22, 9);
    }

    public NeuralInterfaceModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public NeuralInterfaceModel(Function<Identifier, RenderLayer> renderLayerFactory) {
        super(createNormalModelData().createModel(), renderLayerFactory);

        this.setVisible(false);
        this.head.visible = true;
    }
}
