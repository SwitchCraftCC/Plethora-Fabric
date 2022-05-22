package pw.switchcraft.plethora.gameplay.modules.introspection;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IContext;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.SubtargetedModuleMethod;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorModuleItem;
import pw.switchcraft.plethora.integration.EntityIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static pw.switchcraft.plethora.core.ContextHelpers.fromContext;
import static pw.switchcraft.plethora.core.ContextHelpers.fromSubtarget;

public class IntrospectionMethods {
    private static final Identifier MODULE_ID = IntrospectionModuleItem.MODULE_ID;

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_ID = SubtargetedModuleMethod.of(
        "getID", MODULE_ID, EntityIdentifier.class,
        "function():string -- Get this entity's UUID.",
        IntrospectionMethods::getId
    );
    private static FutureMethodResult getId(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        return FutureMethodResult.result(getContext(unbaked).entity.getId().toString());
    }

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_NAME = SubtargetedModuleMethod.of(
        "getName", MODULE_ID, EntityIdentifier.class,
        "function():string -- Get this entity's name.",
        IntrospectionMethods::getName
    );
    private static FutureMethodResult getName(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                              @Nonnull IArguments args) throws LuaException {
        return FutureMethodResult.result(getContext(unbaked).entity.getName());
    }

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_META_OWNER = SubtargetedModuleMethod.of(
        "getMetaOwner", Set.of(MODULE_ID, SensorModuleItem.MODULE_ID), EntityIdentifier.class,
        "function():table -- Get this entity's metadata.",
        IntrospectionMethods::getMetaOwner
    );
    private static FutureMethodResult getMetaOwner(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                   @Nonnull IArguments args) throws LuaException {
        IntrospectionMethodContext ctx = getContext(unbaked);

        MinecraftServer server = ctx.server;
        if (server == null) throw new LuaException("Could not get server instance");

        return FutureMethodResult.result(ctx.context.makePartialChild(ctx.entity.getEntity(server)).getMeta());
    }

    private record IntrospectionMethodContext(IContext<IModuleContainer> context, EntityIdentifier entity,
                                              @Nullable MinecraftServer server) {}
    private static IntrospectionMethodContext getContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IContext<IModuleContainer> ctx = unbaked.bake();
        EntityIdentifier entity = fromSubtarget(ctx, EntityIdentifier.class);

        // Try to get the server from any entity in the context
        MinecraftServer server = null;
        Entity anyEntity = fromContext(ctx, Entity.class);
        if (anyEntity != null) server = anyEntity.getServer();

        return new IntrospectionMethodContext(ctx, entity, server);
    }
}
