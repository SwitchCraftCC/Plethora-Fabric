package pw.switchcraft.plethora.gameplay.modules;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.client.TransformedModel;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3f;
import pw.switchcraft.plethora.api.method.IContextBuilder;
import pw.switchcraft.plethora.api.module.IModuleAccess;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.api.reference.Reference;
import pw.switchcraft.plethora.gameplay.BaseItem;
import pw.switchcraft.plethora.integration.EntityIdentifier;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.Plethora.MOD_ID;
import static pw.switchcraft.plethora.gameplay.modules.ModuleContextHelpers.getEntity;
import static pw.switchcraft.plethora.gameplay.modules.ModuleContextHelpers.getProfile;

public abstract class ModuleItem extends BaseItem implements IModuleHandler {
    public ModuleItem(String itemName, Settings settings) {
        super(itemName, settings);
    }

    @Override
    public String getTranslationKey() {
        return "item." + MOD_ID + ".module.module_" + itemName;
    }

    // TODO: isBlacklisted

    @Nonnull
    public TransformedModel getModel(float delta) {
        return TransformedModel.of(
            this.getDefaultStack(),
            new AffineTransformation(null, Vec3f.POSITIVE_Y.getDegreesQuaternion(delta), null, null)
        );
    }

    @Override
    public void getAdditionalContext(@Nonnull ItemStack stack, @Nonnull IModuleAccess access, @Nonnull IContextBuilder builder) {
        String moduleKey = getModule().toString();

        MinecraftServer server = access.getServer();
        Entity entity = getEntity(server, stack);
        if (entity != null) builder.addContext(moduleKey, entity, Reference.entity(entity));

        GameProfile profile = getProfile(stack);
        if (profile != null) builder.addContext(moduleKey, new EntityIdentifier.Player(profile));
    }
}
