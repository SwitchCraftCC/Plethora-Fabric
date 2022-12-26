package pw.switchcraft.plethora.mixin;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerInteractionManager.class)
public interface ServerPlayerInteractionManagerAccessor {
    @Accessor
    void setMining(boolean mining);

    @Accessor
    int getBlockBreakingProgress();
    @Accessor
    void setBlockBreakingProgress(int progress);
}
