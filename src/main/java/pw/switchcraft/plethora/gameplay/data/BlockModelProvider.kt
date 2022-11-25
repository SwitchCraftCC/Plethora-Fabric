package pw.switchcraft.plethora.gameplay.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.data.client.TextureKey.*
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.math.Direction
import pw.switchcraft.plethora.Plethora.ModId
import pw.switchcraft.plethora.gameplay.registry.Registration.ModBlocks.*
import java.util.*

class BlockModelProvider(out: FabricDataOutput) : FabricModelProvider(out) {
  override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
    gen.registerSimpleCubeAll(REDSTONE_INTEGRATOR)

    registerManipulator(gen, MANIPULATOR_MARK_1, 1)
    registerManipulator(gen, MANIPULATOR_MARK_2, 2)
  }

  override fun generateItemModels(gen: ItemModelGenerator) {
  }

  private fun registerManipulator(gen: BlockStateModelGenerator, block: Block, mark: Int) {
    val modelId = manipulatorModel.upload(
      block,
      TextureMap()
        .put(PARTICLE, ModId("block/manipulator_side"))
        .put(BOTTOM, ModId("block/manipulator_bottom"))
        .put(TOP, ModId("block/manipulator_top$mark"))
        .put(SIDE, ModId("block/manipulator_side")),
      gen.modelCollector
    )

    val variants = createDownDefaultFacingVariantMap()

    gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(
      block,
      BlockStateVariant.create().put(VariantSettings.MODEL, modelId)
    ).coordinate(variants))
  }

  private fun createDownDefaultFacingVariantMap(): BlockStateVariantMap.SingleProperty<Direction> =
    BlockStateVariantMap.create(FACING)
      .register(Direction.DOWN, BlockStateVariant.create())
      .register(Direction.UP, BlockStateVariant.create()
        .put(VariantSettings.X, VariantSettings.Rotation.R180))
      .register(Direction.NORTH, BlockStateVariant.create()
        .put(VariantSettings.X, VariantSettings.Rotation.R90)
        .put(VariantSettings.Y, VariantSettings.Rotation.R180))
      .register(Direction.SOUTH, BlockStateVariant.create()
        .put(VariantSettings.X, VariantSettings.Rotation.R90))
      .register(Direction.EAST, BlockStateVariant.create()
        .put(VariantSettings.X, VariantSettings.Rotation.R90)
        .put(VariantSettings.Y, VariantSettings.Rotation.R270))
      .register(Direction.WEST, BlockStateVariant.create()
        .put(VariantSettings.X, VariantSettings.Rotation.R90)
        .put(VariantSettings.Y, VariantSettings.Rotation.R90))

  companion object {
    val manipulatorModel = Model(
      Optional.of(ModId("block/manipulator_base")), Optional.empty(),
      PARTICLE, BOTTOM, TOP, SIDE
    )
  }
}
