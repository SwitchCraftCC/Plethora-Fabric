package pw.switchcraft.plethora.gameplay.modules.introspection;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.SubtargetedModuleMethod;
import pw.switchcraft.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.ServerContext;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorModuleItem;
import pw.switchcraft.plethora.integration.EntityIdentifier;

import javax.annotation.Nonnull;
import java.util.Set;

import static pw.switchcraft.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.getContext;
import static pw.switchcraft.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.getServerContext;

public class IntrospectionMethods {
    private static final Identifier MODULE_ID = IntrospectionModuleItem.MODULE_ID;

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_ID = SubtargetedModuleMethod.of(
        "getID", MODULE_ID, EntityIdentifier.class,
        "function():string -- Get this entity's UUID.",
        IntrospectionMethods::getId
    );
    private static FutureMethodResult getId(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        return FutureMethodResult.result(getContext(unbaked).entity().getId().toString());
    }

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_NAME = SubtargetedModuleMethod.of(
        "getName", MODULE_ID, EntityIdentifier.class,
        "function():string -- Get this entity's name.",
        IntrospectionMethods::getName
    );
    private static FutureMethodResult getName(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                              @Nonnull IArguments args) throws LuaException {
        return FutureMethodResult.result(getContext(unbaked).entity().getName());
    }

    public static final SubtargetedModuleMethod<EntityIdentifier> GET_META_OWNER = SubtargetedModuleMethod.of(
        "getMetaOwner", Set.of(MODULE_ID, SensorModuleItem.MODULE_ID), EntityIdentifier.class,
        "function():table -- Get this entity's metadata.",
        IntrospectionMethods::getMetaOwner
    );
    private static FutureMethodResult getMetaOwner(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                   @Nonnull IArguments args) throws LuaException {
        ServerContext ctx = getServerContext(unbaked);
        return FutureMethodResult.result(ctx.context().makePartialChild(ctx.entity().getEntity(ctx.server())).getMeta());
    }
}
