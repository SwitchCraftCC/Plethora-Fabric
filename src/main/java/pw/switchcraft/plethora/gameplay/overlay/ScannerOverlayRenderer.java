package pw.switchcraft.plethora.gameplay.overlay;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import pw.switchcraft.plethora.gameplay.modules.LevelableModuleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pw.switchcraft.plethora.util.Config.Scanner.oreColours;

public class ScannerOverlayRenderer extends FlareOverlayRenderer {
    record BlockResult(int x, int y, int z, FlareColor color) {}

    private static final Map<Block, FlareColor> blockColorCache = new HashMap<>();

    private static final List<BlockResult> scanResults = new ArrayList<>();
    private static float scanTimer = 0;

    public static void render(
        ClientPlayerEntity player,
        ItemStack stack,
        MatrixStack matrices,
        float tickDelta,
        float ticks,
        Camera camera
    ) {
        initFlareRenderer(matrices, camera);

        scanTimer += tickDelta;
        if (scanTimer >= 10) {
            scanBlocks(player.getWorld(), player.getBlockPos(), stack);
            scanTimer = 0;
        }

        for (BlockResult result : scanResults) {
            renderFlare(matrices, camera, ticks, result.x + 0.5, result.y + 0.5, result.z + 0.5, result.color, 1.0f);
        }

        uninitFlareRenderer(matrices);
    }

    private static void scanBlocks(World world, BlockPos pos, ItemStack stack) {
        // TODO: Move this to a scanning module class
        final int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        int range = LevelableModuleItem.getEffectiveRange(stack);

        scanResults.clear();

        for (int oX = x - range; oX <= x + range; oX++) {
            for (int oY = y - range; oY <= y + range; oY++) {
                for (int oZ = z - range; oZ <= z + range; oZ++) {
                    BlockState state = world.getBlockState(new BlockPos(oX, oY, oZ));
                    Block block = state.getBlock();

                    if (isBlockOre(state, block)) {
                        scanResults.add(new BlockResult(oX, oY, oZ, getFlareColorByBlock(block)));
                    }
                }
            }
        }
    }

    private static boolean isBlockOre(BlockState state, Block block) {
        if (state == null || block == null || state.isAir()) return false;
        if (block instanceof OreBlock || block instanceof RedstoneOreBlock) return true;
        return state.isIn(ConventionalBlockTags.ORES);
    }

    private static FlareColor getFlareColorByBlock(Block block) {
        if (blockColorCache.containsKey(block)) return blockColorCache.get(block);

        Identifier id = Registry.BLOCK.getId(block);
        FlareColor color = getFlareColorById(oreColours, id);

        blockColorCache.put(block, color);
        return color;
    }
}
