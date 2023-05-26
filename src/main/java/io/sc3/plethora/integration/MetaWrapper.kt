package io.sc3.plethora.integration

import io.sc3.plethora.api.meta.BaseMetaProvider
import io.sc3.plethora.api.method.IPartialContext
import io.sc3.plethora.api.reference.ConstantReference

class MetaWrapper<T : Any>(val value: T) : ConstantReference<MetaWrapper<T>> {
  override fun get(): MetaWrapper<T> = this
  override fun safeGet(): MetaWrapper<T> = this

  object MetaProvider : BaseMetaProvider<MetaWrapper<*>>(
    description = "Simply wraps an object and exposes metadata for that. You can happily ignore this."
  ) {
    override fun getMeta(context: IPartialContext<MetaWrapper<*>>): Map<String, *> =
      context.makePartialChild(context.target.value).meta
  }

  companion object {
    @JvmStatic
    fun <T : Any> of(value: T): MetaWrapper<T> = MetaWrapper(value)
  }
}
