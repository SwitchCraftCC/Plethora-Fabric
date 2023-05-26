package io.sc3.plethora.api.meta

/**
 * Basic wrapper for meta-providers
 */
abstract class BaseMetaProvider<T> @JvmOverloads constructor(
  override val priority: Int = 0,
  override val description: String? = null
) : IMetaProvider<T>
