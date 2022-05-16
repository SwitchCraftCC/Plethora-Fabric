package pw.switchcraft.plethora.gameplay.modules;

import dan200.computercraft.api.client.TransformedModel;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3f;
import pw.switchcraft.plethora.gameplay.BaseItem;

import javax.annotation.Nonnull;

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

    @Nonnull
    public TransformedModel getModel(float delta) {
        return TransformedModel.of(
            this.getDefaultStack(),
            new AffineTransformation(null, Vec3f.POSITIVE_Y.getDegreesQuaternion(delta), null, null)
        );
    }
}
