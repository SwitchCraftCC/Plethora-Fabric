package io.sc3.plethora.gameplay.modules.introspection;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import io.sc3.plethora.api.WorldLocation;
import io.sc3.plethora.api.method.IContext;
import io.sc3.plethora.api.method.IUnbakedContext;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.integration.EntityIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static io.sc3.plethora.core.ContextHelpers.fromContext;
import static io.sc3.plethora.core.ContextHelpers.fromSubtarget;

public class IntrospectionContextHelpers {
    public record IntrospectionContext(IContext<IModuleContainer> context, EntityIdentifier entity,
                                       @Nullable MinecraftServer server) {}
    public static IntrospectionContext getContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IContext<IModuleContainer> ctx = unbaked.bake();
        EntityIdentifier entity = fromSubtarget(ctx, EntityIdentifier.class);

        // Try to get the canvas from any Entity or WorldLocation in the context
        MinecraftServer server = null;
        Entity anyEntity = fromContext(ctx, Entity.class);
        if (anyEntity != null) server = anyEntity.getServer();
        WorldLocation anyLocation = fromContext(ctx, WorldLocation.class);
        if (server == null && anyLocation != null) server = anyLocation.getWorld().getServer();

        return new IntrospectionContext(ctx, entity, server);
    }

    public record ServerContext(IContext<IModuleContainer> context, EntityIdentifier entity,
                                MinecraftServer server) {}
    public static ServerContext getServerContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IntrospectionContext ctx = getContext(unbaked);

        MinecraftServer server = ctx.server;
        if (server == null) throw new LuaException("Could not get canvas instance");

        return new ServerContext(ctx.context, ctx.entity, server);
    }

    public record PlayerContext(IContext<IModuleContainer> context, EntityIdentifier entity,
                                EntityIdentifier.Player player,
                                MinecraftServer server) {}
    public static PlayerContext getPlayerContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        ServerContext ctx = getServerContext(unbaked);

        EntityIdentifier.Player player = fromSubtarget(ctx.context, EntityIdentifier.Player.class);
        if (player == null) throw new LuaException("Must be run on a player");

        return new PlayerContext(ctx.context, ctx.entity, player, ctx.server);
    }
}
