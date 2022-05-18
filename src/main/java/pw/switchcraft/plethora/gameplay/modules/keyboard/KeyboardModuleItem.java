package pw.switchcraft.plethora.gameplay.modules.keyboard;

import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

public class KeyboardModuleItem extends ModuleItem {
    private static final Identifier MODULE_ID = new Identifier(MOD_ID, "module_keyboard");

    public KeyboardModuleItem(Settings settings) {
        super("keyboard", settings);
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return MODULE_ID;
    }
}
