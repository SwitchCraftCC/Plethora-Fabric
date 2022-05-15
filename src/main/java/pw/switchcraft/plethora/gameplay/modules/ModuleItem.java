package pw.switchcraft.plethora.gameplay.modules;

import pw.switchcraft.plethora.gameplay.BaseItem;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

public abstract class ModuleItem extends BaseItem /*implements IModuleHandler*/ {
    public ModuleItem(String itemName, Settings settings) {
        super(itemName, settings);
    }

    @Override
    public String getTranslationKey() {
        return "item." + MOD_ID + ".module.module_" + itemName;
    }

    // TODO: isBlacklisted
    // TODO: initCapabilities
    // TODO: ItemModuleHandler
    // TODO: getProfile
    // TODO: getEntity
    // TODO: getEntityName
}
