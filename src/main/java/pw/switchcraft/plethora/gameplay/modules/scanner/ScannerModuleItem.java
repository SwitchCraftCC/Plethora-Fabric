package pw.switchcraft.plethora.gameplay.modules.scanner;

import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem;
import pw.switchcraft.plethora.util.config.Config.Scanner;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.gameplay.registry.Registration.MOD_ID;

public class ScannerModuleItem extends LevelableModuleItem {
    static final Identifier MODULE_ID = new Identifier(MOD_ID, "scanner");

    public ScannerModuleItem(Settings settings) {
        super("scanner", settings);
    }

    @Override
    public int getBaseRange() {
        return Scanner.radius;
    }

    @Override
    public int getMaxRange() {
        return Scanner.maxRadius;
    }

    @Override
    public int getLevelCost() {
        return Scanner.scanLevelCost;
    }

    @Nonnull
    @Override
    public Identifier getModule() {
        return MODULE_ID;
    }
}
