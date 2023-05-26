package io.sc3.plethora.api.meta

import io.sc3.plethora.api.method.IPartialContext

/**
 * An interface-based version of [IMetaProvider]. One consumes the object directly, rather than needing
 * to use [IPartialContext.getTarget].
 *
 * @param <T> The type of object this provider handles.
 */
@FunctionalInterface
interface SimpleMetaProvider<T> : IMetaProvider<T> {
  override fun getMeta(context: IPartialContext<T>): Map<String, *> = getMeta(context.target)
  fun getMeta(target: T): Map<String, *>
}
