package pw.switchcraft.plethora.integration.vanilla.meta.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pw.switchcraft.plethora.api.meta.BasicMetaProvider;
import pw.switchcraft.plethora.api.reference.BlockReference;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class BlockReferenceMeta extends BasicMetaProvider<BlockReference> {
	public BlockReferenceMeta() {
		super("Provides information about blocks which exist in the world.");
	}

	@Nonnull
	@Override
	public Map<String, ?> getMeta(@Nonnull BlockReference reference) {
		Map<String, Object> data = new HashMap<>();

		BlockState state = reference.getState();
		World world = reference.getLocation().getWorld();
		BlockPos pos = reference.getLocation().getPos();

		data.put("hardness", state.getHardness(world, pos));

		MapColor mapCol = state.getMapColor(world, pos);
		if (mapCol != null) {
			int colour = mapCol.color;
			data.put("colour", colour);
			data.put("color", colour);
		}

		return data;
	}
}
