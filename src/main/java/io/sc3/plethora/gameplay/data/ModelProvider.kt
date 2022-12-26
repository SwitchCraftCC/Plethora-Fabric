package io.sc3.plethora.gameplay.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.data.client.Models.GENERATED
import net.minecraft.data.client.TextureKey.*
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.math.Direction
import io.sc3.plethora.Plethora.ModId
import io.sc3.plethora.gameplay.registry.Registration.ModBlocks.*
import io.sc3.plethora.gameplay.registry.Registration.ModItems
import java.util.*

class ModelProvider(out: FabricDataOutput) : FabricModelProvider(out) {
  override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
    gen.registerSimpleCubeAll(REDSTONE_INTEGRATOR)

    registerManipulator(gen, MANIPULATOR_MARK_1, 1)
    registerManipulator(gen, MANIPULATOR_MARK_2, 2)
  }

  override fun generateItemModels(gen: ItemModelGenerator) {
    gen.register(ModItems.GLASSES_MODULE, GENERATED)
    gen.register(ModItems.INTROSPECTION_MODULE, GENERATED)
    gen.register(ModItems.KEYBOARD_MODULE, GENERATED)
    gen.register(ModItems.KINETIC_MODULE, GENERATED)
    // Laser provided in JSON for custom rotations
    gen.register(ModItems.SCANNER_MODULE, GENERATED)
    gen.register(ModItems.SENSOR_MODULE, GENERATED)
    gen.register(ModItems.NEURAL_CONNECTOR, GENERATED)
    gen.register(ModItems.NEURAL_INTERFACE, GENERATED)
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
