package pw.switchcraft.plethora.mixin;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pw.switchcraft.plethora.gameplay.neural.NeuralInterfaceRecipe;

import java.util.Map;

@Mixin(ShapedRecipe.Serializer.class)
public class ShapedRecipeSerializerMixin {
    @Inject(
        method = "read(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/ShapedRecipe;",
        at = @At("RETURN"),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void readSubType(
        Identifier id,
        JsonObject json,
        CallbackInfoReturnable<ShapedRecipe> cir,
        String group,
        Map<String, Ingredient> map,
        String[] strings,
        int width,
        int height,
        DefaultedList<Ingredient> defaultedList,
        ItemStack itemStack
    ) {
        // Check if the recipe is one of our custom recipes. If it is, call our own recipe constructor.
        String subType = JsonHelper.getString(json, "plethora:subtype", null);
        if (subType == null) return;

        if (subType.equals("plethora:neural_interface")) {
            cir.setReturnValue(new NeuralInterfaceRecipe(id, group, width, height, defaultedList, itemStack));
        }
    }
}
