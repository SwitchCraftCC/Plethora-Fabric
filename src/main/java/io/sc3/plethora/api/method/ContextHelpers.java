package io.sc3.plethora.api.method;

import net.minecraft.item.ItemStack;
import io.sc3.plethora.api.meta.TypedMeta;
import io.sc3.plethora.core.ContextFactory;
import io.sc3.plethora.core.executor.BasicExecutor;
import io.sc3.plethora.integration.DetailsMetaWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ContextHelpers {
	private ContextHelpers() {
	}

	/**
	 * Generate a Lua list with the metadata taken for each element in the list
	 *
	 * @param context The base context to use in getting metadata.
	 * @param list    The list to get items from.
	 * @return The converted list.
	 */
	@Nonnull
	public static <T> List<TypedMeta<T, ?>> getMetaList(@Nonnull IPartialContext<?> context, @Nullable Collection<T> list) {
		if (list == null) return Collections.emptyList();

		List<TypedMeta<T, ?>> out = new ArrayList<>(list.size());
		for (T element : list) {
			out.add(element == null ? null : context.makePartialChild(element).getMeta());
		}

		return out;
	}

	/**
	 * Generate a Lua list with the metadata taken for each element in the list
	 *
	 * @param context The base context to use in getting metadata.
	 * @param list    The list to get items from.
	 * @return The converted list.
	 */
	@Nonnull
	public static <T> List<TypedMeta<T, ?>> getMetaList(@Nonnull IPartialContext<?> context, @Nullable T[] list) {
		if (list == null) return Collections.emptyList();

		List<TypedMeta<T, ?>> map = new ArrayList<>(list.length);
		for (T element : list) {
			map.add(element == null ? null : context.makePartialChild(element).getMeta());
		}

		return map;
	}

	/**
	 * Wrap an {@link ItemStack} so that its metadata is exposed by an in-game call to {@code getMetadata()}
	 *
	 * @param context The base context to use in getting metadata
	 * @param object  The stack to wrap
	 * @return The wrapped stack
	 */
	@Nullable
	public static TypedLuaObject<DetailsMetaWrapper<ItemStack>> wrapStack(@Nonnull IPartialContext<?> context, @Nullable ItemStack object) {
		if (object == null || object.isEmpty()) return null;

		var wrapper = DetailsMetaWrapper.stack(object.copy());
		return context instanceof IContext
			? ((IContext<?>) context).makeChildId(wrapper).getObject()
			: ContextFactory.of(wrapper).withExecutor(BasicExecutor.INSTANCE).getObject();
	}
}
