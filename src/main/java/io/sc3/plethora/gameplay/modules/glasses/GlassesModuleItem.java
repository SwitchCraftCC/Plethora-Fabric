package pw.switchcraft.plethora.gameplay.modules.glasses;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.IContextBuilder;
import pw.switchcraft.plethora.api.module.IModuleAccess;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasServer;
import pw.switchcraft.plethora.util.FakePlayer;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.GLASSES_M;
import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.GLASSES_S;

public class GlassesModuleItem extends ModuleItem {
    public GlassesModuleItem(Settings settings) {
        super("glasses", settings);
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return GLASSES_M;
    }

    @Override
    public void getAdditionalContext(@Nonnull ItemStack stack, @Nonnull IModuleAccess access, @Nonnull IContextBuilder builder) {
        super.getAdditionalContext(stack, access, builder);

        Object owner = access.getOwner();
        if (owner instanceof ServerPlayerEntity player && !(owner instanceof FakePlayer)) {
            CanvasServer glasses = new CanvasServer(access, player);
            builder.addContext(GLASSES_S, glasses).addAttachable(glasses);
        }
    }
}
