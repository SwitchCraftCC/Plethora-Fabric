package pw.switchcraft.plethora.mixin;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleRecipe;

import java.util.Objects;

@Mixin(ShapelessRecipe.Serializer.class)
public class ShapelessRecipeSerializerMixin {
    @Inject(
        method = "read(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/ShapelessRecipe;",
        at = @At("RETURN"),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void readSubType(
        Identifier id,
        JsonObject json,
        CallbackInfoReturnable<ShapelessRecipe> cir,
        String group,
        DefaultedList<Ingredient> defaultedList,
        ItemStack itemStack
    ) {
        // Check if the recipe is one of our custom recipes. If it is, call our own recipe constructor.
        String subType = JsonHelper.getString(json, "plethora:subtype", null);

        if (Objects.equals(subType, "plethora:module_level")) {
            cir.setReturnValue(new LevelableModuleRecipe(id, group, itemStack, defaultedList));
        }
    }
}
