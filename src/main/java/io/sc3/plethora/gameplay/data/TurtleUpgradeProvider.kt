package io.sc3.plethora.gameplay.data

import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.data.DataGenerator
import net.minecraft.item.Item
import io.sc3.plethora.api.module.IModuleHandler
import io.sc3.plethora.gameplay.registry.Registration.ModItems
import io.sc3.plethora.gameplay.registry.Registration.ModTurtleUpgradeSerialisers
import java.util.function.Consumer

class TurtleUpgradeProvider(out: FabricDataOutput) : TurtleUpgradeDataProvider(out) {
  override fun addUpgrades(add: Consumer<Upgrade<TurtleUpgradeSerialiser<*>>>) {
    add.accept(module(ModItems.LASER_MODULE))
    add.accept(module(ModItems.SCANNER_MODULE))
    add.accept(module(ModItems.SENSOR_MODULE))
    add.accept(module(ModItems.INTROSPECTION_MODULE))

    add.accept(
      simpleWithCustomItem(
        ModItems.KINETIC_MODULE.module,
        ModTurtleUpgradeSerialisers.KINETIC_AUGMENT,
        ModItems.KINETIC_MODULE
      )
    )
  }

  fun <T> module(item: T): Upgrade<TurtleUpgradeSerialiser<*>> where T : Item, T : IModuleHandler =
    simpleWithCustomItem(item.module, ModTurtleUpgradeSerialisers.MODULE, item)
}
