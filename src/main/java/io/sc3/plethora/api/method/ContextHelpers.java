package io.sc3.plethora.api.method;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import io.sc3.plethora.api.meta.TypedMeta;
import io.sc3.plethora.core.ContextFactory;
import io.sc3.plethora.core.executor.BasicExecutor;
import io.sc3.plethora.integration.DetailsMetaWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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

	/**
	 * List all items in the inventory,
	 *
	 * @param context The base context to use in getting metadata
	 * @param inventory The inventory to list items in.
	 * @return The wrapped stack
	 * @see #wrapStack(IPartialContext, ItemStack)
	 */
	public static Map<Integer, TypedLuaObject<DetailsMetaWrapper<ItemStack>>> getInventoryItems(@Nonnull IPartialContext<?> context, Inventory inventory) {
		var out = new HashMap<Integer, TypedLuaObject<DetailsMetaWrapper<ItemStack>>>();

		for (int i = 0, size = inventory.size(); i < size; i++) {
			var item = wrapStack(context, inventory.getStack(i));
			if (item != null) out.put(i + 1, item);
		}

		return out;
	}
}
