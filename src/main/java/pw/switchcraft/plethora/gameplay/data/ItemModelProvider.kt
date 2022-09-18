package pw.switchcraft.plethora.gameplay.data

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models.GENERATED
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems

class ItemModelProvider(generator: FabricDataGenerator) : FabricModelProvider(generator) {
  override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
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
}
