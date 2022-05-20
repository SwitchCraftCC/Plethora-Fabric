package pw.switchcraft.plethora.gameplay.client.block;

import dan200.computercraft.api.client.TransformedModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlockEntity;
import pw.switchcraft.plethora.gameplay.manipulator.ManipulatorType;
import pw.switchcraft.plethora.util.MatrixHelpers;

import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock.OFFSET;

public class ManipulatorRenderer implements BlockEntityRenderer<ManipulatorBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final BakedModel missingModel;

    public ManipulatorRenderer(BlockEntityRendererFactory.Context ctx) {
        itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        missingModel = MinecraftClient.getInstance().getBakedModelManager().getMissingModel();
    }

    @Override
    public void render(ManipulatorBlockEntity manipulator, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.multiplyPositionMatrix(MatrixHelpers.matrixFor(manipulator.getFacing()));
        matrices.translate(0, OFFSET, 0);

        ManipulatorType type = manipulator.getManipulatorType();
        float rotation = manipulator.incrementRotation(tickDelta);

        int size = type.size();
        Box[] boxes = type.boxesFor(Direction.DOWN);
        for (int i = 0; i < size; i++) {
            ItemStack stack = manipulator.getStack(i);
            if (stack.isEmpty()) continue;

            Box box = boxes[i];

            matrices.push();

            matrices.translate(
                (box.minX + box.maxX) / 2.0f,
                type.scale,
                (box.minZ + box.maxZ) / 2.0f
            );

            BakedModel model = missingModel;
            if (stack.getItem() instanceof IModuleHandler handler) {
                TransformedModel transformed = handler.getModel(rotation);
                transformed.push(matrices);
                model = transformed.getModel();
            }

            matrices.scale(type.scale, type.scale, type.scale);
            matrices.translate(0.0f, -0.2f, 0.0f); // ItemRenderer already translates by -0.5f, -0.5f, -0.5f

            // Be sure to use the NONE mode here, GROUND and other modes imply a default translation and scale
            itemRenderer.renderItem(stack, Mode.NONE, false, matrices, vertexConsumers, light, overlay, model);
            matrices.pop(); // TransformedModel.push will push to our matrix stack

            matrices.pop();
        }

        matrices.pop();
    }

    @Override
    public int getRenderDistance() {
        return 32;
    }
}
