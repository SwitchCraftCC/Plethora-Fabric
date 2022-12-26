package pw.switchcraft.plethora.gameplay.modules.keyboard;

import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.KEYBOARD_M;

public class KeyboardModuleItem extends ModuleItem {
    public KeyboardModuleItem(Settings settings) {
        super("keyboard", settings);
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return KEYBOARD_M;
    }
}
