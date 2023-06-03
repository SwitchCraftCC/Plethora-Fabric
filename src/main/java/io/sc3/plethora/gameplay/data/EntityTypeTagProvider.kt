package io.sc3.plethora.gameplay.data

import io.sc3.plethora.gameplay.PlethoraEntityTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class EntityTypeTagProvider(
  out: FabricDataOutput,
  future: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<EntityType<*>>(out, RegistryKeys.ENTITY_TYPE, future) {
  override fun configure(arg: RegistryWrapper.WrapperLookup) {
    getOrCreateTagBuilder(PlethoraEntityTags.LASERS_PROVIDE_ENERGY)
      .add(EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.ZOMBIFIED_PIGLIN)
  }
}

