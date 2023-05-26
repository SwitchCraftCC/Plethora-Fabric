package io.sc3.plethora.gameplay.modules

import io.sc3.plethora.api.method.IAttachable
import io.sc3.plethora.core.AttachableWrapperPeripheral
import io.sc3.plethora.core.RegisteredMethod
import io.sc3.plethora.core.UnbakedContext
import io.sc3.plethora.core.executor.TaskRunner
import net.minecraft.util.Pair

class ModulePeripheral(
  name: String,
  owner: Any?,
  methods: Pair<List<RegisteredMethod<*>>, List<UnbakedContext<*>>>,
  runner: TaskRunner,
  attachments: Collection<IAttachable>,
  private val stackHash: Int
) : AttachableWrapperPeripheral(name, owner, methods, runner, attachments) {
  override fun equals(other: Any?) =
    super.equals(other) && other is ModulePeripheral && stackHash == other.stackHash

  override fun hashCode(): Int = stackHash
}
