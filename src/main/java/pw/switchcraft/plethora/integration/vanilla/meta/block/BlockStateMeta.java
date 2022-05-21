package pw.switchcraft.plethora.integration.vanilla.meta.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.state.property.Property;
import pw.switchcraft.plethora.api.meta.BaseMetaProvider;
import pw.switchcraft.plethora.api.method.IPartialContext;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class BlockStateMeta extends BaseMetaProvider<BlockState> {
    public BlockStateMeta() {
        super("Provides some very basic information about a block and its associated state.");
    }

    @Nonnull
    @Override
    public Map<String, ?> getMeta(@Nonnull IPartialContext<BlockState> context) {
        BlockState state = context.getTarget();
        Block block = state.getBlock();

        Map<String, Object> data = new HashMap<>();
        fillBasicMeta(data, state);

        Material material = state.getMaterial();
        data.put("material", context.makePartialChild(material).getMeta());

        // TODO: block meta harvest level
        // int level = block.getHarvestLevel(state);
        // if (level >= 0) data.put("harvestLevel", level);
        // data.put("harvestTool", block.getHarvestTool(state));

        return data;
    }

    public static void fillBasicMeta(@Nonnull Map<? super String, Object> data, @Nonnull BlockState state) {
        Block block = state.getBlock();

        // TODO
        // data.put("metadata", block.getMetaFromState(state));

        Map<Property<?>, Comparable<?>> properties = state.getEntries();
        Map<Object, Object> propertyMap;
        if (properties.isEmpty()) {
            propertyMap = Collections.emptyMap();
        } else {
            propertyMap = new HashMap<>(properties.size());
            for (Map.Entry<Property<?>, Comparable<?>> item : properties.entrySet()) {
                Object value = item.getValue();
                if (!(value instanceof String) && !(value instanceof Number) && !(value instanceof Boolean)) {
                    value = value.toString();
                }
                propertyMap.put(item.getKey().getName(), value);
            }
        }
        data.put("state", propertyMap);
    }
}
