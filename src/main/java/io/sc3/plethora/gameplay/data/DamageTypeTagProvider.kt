package io.sc3.plethora.gameplay.data

import io.sc3.plethora.gameplay.registry.Registration.ModDamageSources
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.DamageTypeTags
import java.util.concurrent.CompletableFuture

class DamageTypeTagProvider(
  out: FabricDataOutput,
  future: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<DamageType>(out, RegistryKeys.DAMAGE_TYPE, future) {
  override fun configure(arg: RegistryWrapper.WrapperLookup) {
    getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE).add(ModDamageSources.LASER)
    getOrCreateTagBuilder(DamageTypeTags.IS_FIRE).add(ModDamageSources.LASER)
  }
}

