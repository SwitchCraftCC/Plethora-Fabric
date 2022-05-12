package pw.switchcraft.plethora.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.gameplay.data.RecipeGenerator;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply", at = @At("HEAD"))
    public void interceptApply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        Plethora.LOG.info("Applying custom recipes");

        // Forcibly generate the custom recipes at runtime
        RecipeGenerator generator = new RecipeGenerator(null);

        // Fake exporter
        Consumer<RecipeJsonProvider> exporter = provider -> {
            JsonObject recipeJson = provider.toJson();
            map.put(provider.getRecipeId(), recipeJson);
        };

        generator.generateRecipes(exporter);
    }
}
