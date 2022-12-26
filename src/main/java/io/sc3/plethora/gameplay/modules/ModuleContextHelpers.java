package pw.switchcraft.plethora.gameplay.modules;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import pw.switchcraft.plethora.util.EntityHelpers;

import javax.annotation.Nullable;
import java.util.UUID;

import static net.minecraft.nbt.NbtElement.NUMBER_TYPE;
import static net.minecraft.nbt.NbtElement.STRING_TYPE;

public class ModuleContextHelpers {
    @Nullable
    public static UUID getUuid(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null
            || !nbt.contains("id_lower", NUMBER_TYPE)
            || !nbt.contains("id_upper", NUMBER_TYPE)) return null;
        return new UUID(nbt.getLong("id_upper"), nbt.getLong("id_lower"));
    }

    public static GameProfile getProfile(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        UUID uuid = getUuid(stack);
        if (nbt == null || uuid == null) return null;
        return new GameProfile(uuid, nbt.getString("bound_name"));
    }

    public static Entity getEntity(MinecraftServer server, ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        UUID uuid = getUuid(stack);
        if (nbt == null || uuid == null) return null;
        return EntityHelpers.getEntityFromUuid(server, uuid);
    }

    public static String getEntityName(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains("bound_name", STRING_TYPE) ? nbt.getString("bound_name") : null;
    }

    public static int getLevel(ItemStack stack) {
        return LevelableModuleItem.getLevel(stack);
    }
}
