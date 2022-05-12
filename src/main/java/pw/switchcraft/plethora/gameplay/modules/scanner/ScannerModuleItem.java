package pw.switchcraft.plethora.gameplay.modules.scanner;

import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem;
import pw.switchcraft.plethora.util.Config.Scanner;

public class ScannerModuleItem extends LevelableModuleItem {
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
}
