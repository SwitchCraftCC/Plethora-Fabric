package pw.switchcraft.plethora.gameplay.modules.sensor;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pw.switchcraft.plethora.api.IWorldLocation;
import pw.switchcraft.plethora.api.method.ContextKeys;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IContext;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.SubtargetedModuleMethod;
import pw.switchcraft.plethora.api.reference.Reference;
import pw.switchcraft.plethora.gameplay.modules.RangeInfo;
import pw.switchcraft.plethora.integration.vanilla.entity.EntityMeta;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static pw.switchcraft.plethora.gameplay.modules.sensor.SensorHelpers.*;

public class SensorMethods {
    private static final Identifier MODULE_ID = SensorModuleItem.MODULE_ID;

    public static final SubtargetedModuleMethod<IWorldLocation> SENSE = SubtargetedModuleMethod.of(
        "sense", MODULE_ID, IWorldLocation.class,
        "function():table -- Scan for entities in the vicinity",
        SensorMethods::sense
    );

    public static final SubtargetedModuleMethod<IWorldLocation> GET_META_BY_ID = SubtargetedModuleMethod.of(
        "getMetaByID", MODULE_ID, IWorldLocation.class,
        "-- Find a nearby entity by UUID",
        SensorMethods::getMetaById
    );

    public static final SubtargetedModuleMethod<IWorldLocation> GET_META_BY_NAME = SubtargetedModuleMethod.of(
        "getMetaByName", MODULE_ID, IWorldLocation.class,
        "-- Find a nearby entity by name",
        SensorMethods::getMetaByName
    );

    private static FutureMethodResult sense(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        SensorMethodContext ctx = getContext(unbaked);
        World world = ctx.loc.getWorld();
        BlockPos pos = ctx.loc.getPos();

        return ctx.context.getCostHandler().await(ctx.range.getBulkCost(), () -> {
            List<Entity> entities = world.getEntitiesByClass(Entity.class,
                getBox(pos, ctx.range.getRange()), DEFAULT_PREDICATE);

            // TODO: Helpers.map?
            return FutureMethodResult.result(entities.stream()
                .map(x -> EntityMeta.getBasicProperties(x, ctx.loc))
                .toList());
        });
    }

    private static FutureMethodResult getMetaById(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                  @Nonnull IArguments args) throws LuaException {
        SensorMethodContext ctx = getContext(unbaked);
        int radius = ctx.range.getRange();

        Entity entity = findEntityByUuid(ctx.loc, radius, UUID.fromString(args.getString(0)));
        if (entity == null) return null;

        return FutureMethodResult.result(ctx.context.makeChild(entity, Reference.bounded(entity, ctx.loc, radius))
            .getMeta());
    }

    private static FutureMethodResult getMetaByName(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                    @Nonnull IArguments args) throws LuaException {
        SensorMethodContext ctx = getContext(unbaked);
        int radius = ctx.range.getRange();

        Entity entity = findEntityByName(ctx.loc, radius, args.getString(0));
        if (entity == null) return null;

        return FutureMethodResult.result(ctx.context.makeChild(entity, Reference.bounded(entity, ctx.loc, radius))
            .getMeta());
    }

    private record SensorMethodContext(IContext<IModuleContainer> context, IWorldLocation loc, RangeInfo range) {}
    private static SensorMethodContext getContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IContext<IModuleContainer> context = unbaked.bake();
        IWorldLocation location = context.getContext(ContextKeys.ORIGIN, IWorldLocation.class);
        RangeInfo range = context.getContext(SensorModuleItem.MODULE_ID, RangeInfo.class);
        return new SensorMethodContext(context, location, range);
    }
}
