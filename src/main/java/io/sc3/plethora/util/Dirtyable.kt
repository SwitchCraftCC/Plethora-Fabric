package pw.switchcraft.plethora.util

interface Dirtyable {
  fun pollDirty(): Boolean
  fun setDirty()
}
