package io.sc3.plethora.api.module;

import net.minecraft.item.ItemStack;
import io.sc3.plethora.api.method.IContextBuilder;

import javax.annotation.Nonnull;

/**
 * Some default implementations for {@link IModuleHandler} methods.
 *
 * It is recommended that all implementations extend this class: it allows additional methods
 * to be added to the module handler without introducing abstract method errors occurring.
 */
public abstract class AbstractModuleHandler implements IModuleHandler {
	@Override
	public void getAdditionalContext(@Nonnull ItemStack stack, @Nonnull IModuleAccess access, @Nonnull IContextBuilder builder) {
	}
}
