package pw.switchcraft.plethora.api.module;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.IContextBuilder;

import javax.annotation.Nonnull;

/**
 * A capability which provides a module
 */
public interface IModuleHandler {
    /**
     * Get the module from this item
     *
     * @return The module.
     */
    @Nonnull
    Identifier getModule();

    /**
     * Used to get additional context from a stack
     *
     * @param stack   The stack to get additional context for.
     * @param access  The module access we are using.
     * @param builder The builder to add additional context to.
     */
    void getAdditionalContext(@Nonnull ItemStack stack, @Nonnull IModuleAccess access, @Nonnull IContextBuilder builder);

    // TODO: Implement getModel or just always delegate to the vanilla item renderer?
}
