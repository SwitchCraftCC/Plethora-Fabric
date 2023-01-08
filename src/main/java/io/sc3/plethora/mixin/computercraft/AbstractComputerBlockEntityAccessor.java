package io.sc3.plethora.mixin.computercraft;

import dan200.computercraft.shared.computer.blocks.AbstractComputerBlockEntity;
import net.minecraft.inventory.ContainerLock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractComputerBlockEntity.class)
public interface AbstractComputerBlockEntityAccessor {
  @Accessor
  ContainerLock getLockCode();
}
