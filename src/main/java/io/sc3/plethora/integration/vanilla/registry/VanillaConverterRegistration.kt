package io.sc3.plethora.integration.vanilla.registry

import io.sc3.plethora.api.converter.IConverter
import io.sc3.plethora.api.converter.IConverterRegistry
import io.sc3.plethora.integration.vanilla.converter.VanillaConverters
import net.minecraft.util.Identifier.DEFAULT_NAMESPACE

object VanillaConverterRegistration {
  @JvmStatic
  fun registerConverters(registry: IConverterRegistry) {
    with (registry) {
      converter("getStackItem", VanillaConverters.GET_STACK_ITEM)
      converter("getBlockReferenceBlock", VanillaConverters.GET_BLOCK_REFERENCE_BLOCK)
      converter("getBlockReferenceBlockEntity", VanillaConverters.GET_BLOCK_REFERENCE_BLOCK_ENTITY)
      converter("getBlockEntityReference", VanillaConverters.GET_BLOCK_ENTITY_REFERENCE)
      converter("getBlockStateBlock", VanillaConverters.GET_BLOCK_STATE_BLOCK)
      converter("getEntityIdentifier", VanillaConverters.GET_ENTITY_IDENTIFIER)
    }
  }

  private inline fun <reified T> IConverterRegistry.converter(name: String, provider: IConverter<T, *>) {
    registerConverter(name, DEFAULT_NAMESPACE, T::class.java, provider)
  }
}
