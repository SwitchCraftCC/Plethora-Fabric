package io.sc3.plethora.gameplay.modules.keyboard;

import net.minecraft.util.Identifier;
import io.sc3.plethora.gameplay.modules.ModuleItem;

import javax.annotation.Nonnull;

import static io.sc3.plethora.gameplay.registry.PlethoraModules.KEYBOARD_M;

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
