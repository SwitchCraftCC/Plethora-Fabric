package io.sc3.plethora.gameplay.data

import io.sc3.plethora.gameplay.data.recipes.RecipeGenerator
import io.sc3.plethora.gameplay.registry.Registration
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys
import org.slf4j.LoggerFactory

object PlethoraDatagen : DataGeneratorEntrypoint {
  val log = LoggerFactory.getLogger("Plethora/PlethoraDatagen")!!

  override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
    log.info("Plethora datagen initializing")

    val pack = generator.createPack()
    pack.addProvider(::TurtleUpgradeProvider)
    pack.addProvider(::PocketUpgradeProvider)
    pack.addProvider(::ModelProvider)
    pack.addProvider(::BlockLootTableProvider)
    pack.addProvider(::BlockTagProvider)
    pack.addProvider(::ItemTagProvider)
    pack.addProvider(::DamageTypeTagProvider)
    pack.addProvider(::DynamicRegistryProvider)
    pack.addProvider(::EntityTypeTagProvider)
    pack.addProvider { out, _ -> RecipeGenerator(out) }
  }

  override fun buildRegistry(registryBuilder: RegistryBuilder) {
    registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, Registration::bootstrapDamageTypes)
  }
}
