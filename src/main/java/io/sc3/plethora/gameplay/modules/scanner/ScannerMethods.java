package io.sc3.plethora.gameplay.modules.scanner;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import io.sc3.plethora.api.IWorldLocation;
import io.sc3.plethora.api.WorldLocation;
import io.sc3.plethora.api.method.FutureMethodResult;
import io.sc3.plethora.api.method.IContext;
import io.sc3.plethora.api.method.IUnbakedContext;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.api.module.SubtargetedModuleMethod;
import io.sc3.plethora.api.reference.BlockReference;
import io.sc3.plethora.gameplay.modules.RangeInfo;
import io.sc3.plethora.integration.vanilla.meta.block.BlockStateMeta;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.sc3.plethora.api.method.ArgumentExt.assertIntBetween;
import static io.sc3.plethora.api.method.ContextKeys.ORIGIN;
import static io.sc3.plethora.core.ContextHelpers.fromContext;
import static io.sc3.plethora.gameplay.registry.PlethoraModules.SCANNER_M;
import static io.sc3.plethora.gameplay.registry.PlethoraModules.SCANNER_S;

public class ScannerMethods {
    public static final SubtargetedModuleMethod<IWorldLocation> SCAN = SubtargetedModuleMethod.of(
        "scan", SCANNER_M, IWorldLocation.class,
        "function():table -- Scan all blocks in the vicinity",
        ScannerMethods::scan
    );
    private static FutureMethodResult scan(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                           @Nonnull IArguments args) throws LuaException {
        ScannerMethodContext ctx = getContext(unbaked);
        World world = ctx.loc.getWorld();
        BlockPos pos = ctx.loc.getPos();
        final int x = pos.getX(), y = pos.getY(), z = pos.getZ();

        return ctx.context.getCostHandler().await(ctx.range.getBulkCost(), () ->
            FutureMethodResult.result(scan(world, x, y, z, ctx.range.getRange())));
    }

    private static List<Map<String, ?>> scan(World world, int x, int y, int z, int radius) {
        List<Map<String, ?>> result = new ArrayList<>();
        for (int oX = x - radius; oX <= x + radius; oX++) {
            for (int oY = y - radius; oY <= y + radius; oY++) {
                for (int oZ = z - radius; oZ <= z + radius; oZ++) {
                    BlockPos subPos = BlockPos.ofFloored(oX, oY, oZ);
                    BlockState block = world.getBlockState(subPos);

                    HashMap<String, Object> data = new HashMap<>(6);
                    data.put("x", oX - x);
                    data.put("y", oY - y);
                    data.put("z", oZ - z);

                    Identifier name = Registries.BLOCK.getId(block.getBlock());
                    data.put("name", name.toString());

                    BlockStateMeta.fillBasicMeta(data, block);

                    result.add(data);
                }
            }
        }

        return result;
    }

    public static final SubtargetedModuleMethod<IWorldLocation> GET_BLOCK_META = SubtargetedModuleMethod.of(
        "getBlockMeta", SCANNER_M, IWorldLocation.class,
        "function(x:integer, y:integer, z:integer):table|nil -- -- Get metadata about a nearby block",
        ScannerMethods::getBlockMeta
    );
    private static FutureMethodResult getBlockMeta(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                   @Nonnull IArguments args) throws LuaException {
        ScannerMethodContext ctx = getContext(unbaked);
        int radius = ctx.range.getRange();

        int x = assertIntBetween(args, 0, -radius, radius, "X coordinate out of bounds (%s)");
        int y = assertIntBetween(args, 1, -radius, radius, "Y coordinate out of bounds (%s)");
        int z = assertIntBetween(args, 2, -radius, radius, "Z coordinate out of bounds (%s)");

        return FutureMethodResult.result(ctx.context
            .makeChild(new BlockReference(new WorldLocation(ctx.loc.getWorld(), ctx.loc.getPos().add(x, y, z))))
            .getMeta());
    }

    private record ScannerMethodContext(IContext<IModuleContainer> context, IWorldLocation loc, RangeInfo range) {}
    private static ScannerMethodContext getContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IContext<IModuleContainer> ctx = unbaked.bake();
        IWorldLocation location = fromContext(ctx, IWorldLocation.class, ORIGIN);
        RangeInfo range = fromContext(ctx, RangeInfo.class, SCANNER_S);
        return new ScannerMethodContext(ctx, location, range);
    }
}
