package pw.switchcraft.plethora.gameplay.modules.keyboard;

import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.gameplay.modules.ModuleItem;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

public class KeyboardModuleItem extends ModuleItem {
    public static final Identifier MODULE_ID = new Identifier(MOD_ID, "keyboard");

    public KeyboardModuleItem(Settings settings) {
        super("keyboard", settings);
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return MODULE_ID;
    }
}
