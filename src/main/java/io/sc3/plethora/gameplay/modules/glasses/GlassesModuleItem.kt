package io.sc3.plethora.gameplay.modules.glasses;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import io.sc3.plethora.api.method.IContextBuilder;
import io.sc3.plethora.api.module.IModuleAccess;
import io.sc3.plethora.gameplay.modules.ModuleItem;
import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasServer;
import io.sc3.plethora.util.FakePlayer;

import javax.annotation.Nonnull;

import static io.sc3.plethora.gameplay.registry.PlethoraModules.GLASSES_M;
import static io.sc3.plethora.gameplay.registry.PlethoraModules.GLASSES_S;

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
