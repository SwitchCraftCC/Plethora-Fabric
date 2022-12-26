package pw.switchcraft.plethora.gameplay.modules;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.Pair;
import pw.switchcraft.plethora.api.method.IAttachable;
import pw.switchcraft.plethora.core.AttachableWrapperPeripheral;
import pw.switchcraft.plethora.core.RegisteredMethod;
import pw.switchcraft.plethora.core.UnbakedContext;
import pw.switchcraft.plethora.core.executor.TaskRunner;

import java.util.Collection;
import java.util.List;

public class ModulePeripheral extends AttachableWrapperPeripheral {
    private final int stackHash;

    public ModulePeripheral(
        String name, Object owner, Pair<List<RegisteredMethod<?>>, List<UnbakedContext<?>>> methods, TaskRunner runner,
        Collection<IAttachable> attachments, int stackHash
    ) {
        super(name, owner, methods, runner, attachments);
        this.stackHash = stackHash;
    }

    @Override
    public boolean equals(IPeripheral other) {
        return super.equals(other) && other instanceof ModulePeripheral
            && stackHash == ((ModulePeripheral) other).stackHash;
    }
}
