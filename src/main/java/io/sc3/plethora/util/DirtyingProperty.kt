package io.sc3.plethora.util

import kotlin.reflect.KProperty

class DirtyingProperty<T, U : Dirtyable>(
  default: T,
  private val onChange: ((old: T, new: T, dirtyable: U) -> Unit)? = null
) {
  var value: T = default

  operator fun getValue(thisRef: U, property: KProperty<*>) = value
  operator fun setValue(thisRef: U, property: KProperty<*>, value: T) {
    if (this.value != value) {
      this.value = value
      onChange?.invoke(this.value, value, thisRef)
      thisRef.setDirty()
    }
  }
}
