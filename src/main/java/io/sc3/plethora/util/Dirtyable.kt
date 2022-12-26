package io.sc3.plethora.util

interface Dirtyable {
  fun pollDirty(): Boolean
  fun setDirty()
}
