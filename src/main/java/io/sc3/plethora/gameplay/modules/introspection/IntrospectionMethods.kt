package io.sc3.plethora.gameplay.modules.introspection;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import io.sc3.plethora.api.method.FutureMethodResult;
import io.sc3.plethora.api.method.IUnbakedContext;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.api.module.SubtargetedModuleMethod;
import io.sc3.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.ServerContext;
import io.sc3.plethora.integration.EntityIdentifier;

import javax.annotation.Nonnull;
import java.util.Set;

import static io.sc3.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.getContext;
import static io.sc3.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.getServerContext;
import static io.sc3.plethora.gameplay.registry.PlethoraModules.INTROSPECTION_M;
import static io.sc3.plethora.gameplay.registry.PlethoraModules.SENSOR_M;

public final class IntrospectionMethods {
    public static final SubtargetedModuleMethod<EntityIdentifier> GET_ID = SubtargetedModuleMethod.of(
        "getID", INTROSPECTION_M, EntityIdentifier.class,
        "function():string -- Get this entity's UUID.",
        IntrospectionMethods::getId
    );
    private static FutureMethodResult getId(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        return FutureMethodResult.result(getContext(unbaked).entity().getId().toString());
    }

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_NAME = SubtargetedModuleMethod.of(
        "getName", INTROSPECTION_M, EntityIdentifier.class,
        "function():string -- Get this entity's name.",
        IntrospectionMethods::getName
    );
    private static FutureMethodResult getName(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                              @Nonnull IArguments args) throws LuaException {
        return FutureMethodResult.result(getContext(unbaked).entity().getName());
    }

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_META_OWNER = SubtargetedModuleMethod.of(
        "getMetaOwner", Set.of(INTROSPECTION_M, SENSOR_M), EntityIdentifier.class,
        "function():table -- Get this entity's metadata.",
        IntrospectionMethods::getMetaOwner
    );
    private static FutureMethodResult getMetaOwner(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                   @Nonnull IArguments args) throws LuaException {
        ServerContext ctx = getServerContext(unbaked);
        return FutureMethodResult.result(ctx.context().makePartialChild(ctx.entity().getEntity(ctx.server())).getMeta());
    }
}
