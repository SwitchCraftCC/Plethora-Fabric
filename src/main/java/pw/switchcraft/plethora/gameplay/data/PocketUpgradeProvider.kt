package pw.switchcraft.plethora.gameplay.data

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.data.DataGenerator
import net.minecraft.item.Item
import pw.switchcraft.plethora.api.module.IModuleHandler
import pw.switchcraft.plethora.gameplay.registry.Registration
import pw.switchcraft.plethora.gameplay.registry.Registration.ModItems
import java.util.function.Consumer

class PocketUpgradeProvider(out: FabricDataOutput) : PocketUpgradeDataProvider(out) {
  override fun addUpgrades(add: Consumer<Upgrade<PocketUpgradeSerialiser<*>>>) {
    add.accept(module(ModItems.LASER_MODULE))
    add.accept(module(ModItems.SCANNER_MODULE))
    add.accept(module(ModItems.SENSOR_MODULE))
    add.accept(module(ModItems.INTROSPECTION_MODULE))
    add.accept(module(ModItems.KINETIC_MODULE))
    add.accept(module(ModItems.KEYBOARD_MODULE))
  }

  fun <T> module(item: T): Upgrade<PocketUpgradeSerialiser<*>> where T : Item, T : IModuleHandler =
    simpleWithCustomItem(item.module, Registration.ModPocketUpgradeSerialisers.MODULE, item)
}
