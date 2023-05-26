package io.sc3.plethora.api.meta

import io.sc3.plethora.api.method.IPartialContext

/**
 * Provides metadata about an object.
 *
 * @see IMetaRegistry
 * @see IPartialContext
 */
fun interface IMetaProvider<T> {
  /**
   * Get metadata about an object
   *
   * @param context The object to get metadata about
   * @return The gathered data. Do not return `null`.
   */
  fun getMeta(context: IPartialContext<T>): Map<String, *>

  /**
   * Get the priority of this provider
   *
   * [Integer.MIN_VALUE] is the lowest priority and [Integer.MAX_VALUE] is the highest. Providers
   * with higher priorities will be preferred.
   *
   * @return The provider's priority
   */
  val priority: Int
    get() = 0

  /**
   * Get a basic description of this meta provider
   *
   * @return This provider's description, or `null` if none is available.
   */
  val description: String?
    get() = null

  /**
   * Get an example input for this meta provider
   *
   * @return An example input for this meta provider, or `null` if none is available.
   */
  val example: T?
    get() = null
}
