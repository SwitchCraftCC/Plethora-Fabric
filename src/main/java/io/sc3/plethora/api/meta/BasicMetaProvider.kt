package io.sc3.plethora.api.meta

import io.sc3.plethora.api.method.IPartialContext

/**
 * Basic wrapper for meta-providers
 */
abstract class BasicMetaProvider<T> @JvmOverloads constructor(
  priority: Int = 0,
  description: String? = null
) : BaseMetaProvider<T>(priority, description), SimpleMetaProvider<T> {
  override fun getMeta(context: IPartialContext<T>) = getMeta(context.target)
}
