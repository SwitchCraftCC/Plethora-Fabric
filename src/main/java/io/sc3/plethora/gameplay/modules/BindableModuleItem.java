package io.sc3.plethora.gameplay.modules;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public abstract class BindableModuleItem extends ModuleItem {
    public BindableModuleItem(String itemName, Settings settings) {
        super(itemName, settings);
    }

    public TypedActionResult<ItemStack> onBindableModuleUse(World world, PlayerEntity player, Hand hand) {
        return TypedActionResult.success(player.getStackInHand(hand));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // TODO: Check module blacklist here

        if (world.isClient) return TypedActionResult.success(stack);

        GameProfile profile = player.getGameProfile();
        if (player.isSneaking() && !profile.getName().startsWith("[") && profile.getId() != null) {
            NbtCompound nbt = stack.getOrCreateNbt();

            if (profile.equals(ModuleContextHelpers.getProfile(stack))) {
                // Remove the binding if we're already bound
                nbt.remove("id_lower");
                nbt.remove("id_upper");
                nbt.remove("bound_name");
                // If our tag is now empty, clear it - turtle/pocket upgrades require NBT to be exactly the same as the
                // template item.
                // TODO: Is this still the case in 1.18 CC? Can we alter this behaviour?
                if (nbt.isEmpty()) stack.setNbt(null);

                player.sendMessage(Text.translatable(getTranslationKey() + ".cleared", player.getName()), true);
            } else {
                // Otherwise, bind to the current player
                UUID id = profile.getId();
                nbt.putLong("id_lower", id.getLeastSignificantBits());
                nbt.putLong("id_upper", id.getMostSignificantBits());
                nbt.putString("bound_name", profile.getName());

                player.sendMessage(Text.translatable(getTranslationKey() + ".bound", player.getName()), true);
            }
        } else {
            return onBindableModuleUse(world, player, hand);
        }

        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        String boundName = ModuleContextHelpers.getEntityName(stack);
        if (boundName != null) {
            tooltip.add(Text.translatable(getTranslationKey() + ".binding", boundName));
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        // New in 1.18: Show a glint on bound modules to help the user differentiate them from unbound ones, at least
        // if/while CC still prevents you from equipping bound modules.
        return ModuleContextHelpers.getUuid(stack) != null;
    }
}
