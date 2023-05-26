package io.sc3.plethora.integration.vanilla.meta.block;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import io.sc3.plethora.api.meta.BasicMetaProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class BlockMeta extends BasicMetaProvider<Block> {
	public BlockMeta() {
		super("Provide the registry name, display name and translation key of a block.");
	}

	@Nonnull
	@Override
	public Map<String, ?> getMeta(@Nonnull Block block) {
		return getBasicMeta(block);
	}

	@Nonnull
	public static Map<String, ?> getBasicMeta(@Nonnull Block block) {
		HashMap<String, Object> data = new HashMap<>(3);

		Identifier name = Registries.BLOCK.getId(block);
		data.put("name", name.toString());

		data.put("displayName", block.getName().getString());
		data.put("translationKey", block.getTranslationKey());

		return data;
	}
}
