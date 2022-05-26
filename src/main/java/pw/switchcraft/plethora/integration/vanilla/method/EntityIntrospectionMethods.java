package pw.switchcraft.plethora.integration.vanilla.method;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IContext;
import pw.switchcraft.plethora.api.method.IUnbakedContext;
import pw.switchcraft.plethora.api.method.TypedLuaObject;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.SubtargetedModuleMethod;
import pw.switchcraft.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.PlayerContext;
import pw.switchcraft.plethora.integration.EntityIdentifier;
import pw.switchcraft.plethora.util.EquipmentInventoryWrapper;
import pw.switchcraft.plethora.util.RangedInventoryWrapper;

import javax.annotation.Nonnull;

import static net.minecraft.entity.player.PlayerInventory.MAIN_SIZE;
import static pw.switchcraft.plethora.gameplay.modules.introspection.IntrospectionContextHelpers.getPlayerContext;
import static pw.switchcraft.plethora.gameplay.registry.PlethoraModules.INTROSPECTION_M;

public final class EntityIntrospectionMethods {
    public static final SubtargetedModuleMethod<EntityIdentifier.Player> GET_INVENTORY = SubtargetedModuleMethod.of(
        "getInventory", INTROSPECTION_M, EntityIdentifier.Player.class,
        "function():table -- Get this player's inventory",
        EntityIntrospectionMethods::getInventory
    );
    private static FutureMethodResult getInventory(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                   @Nonnull IArguments args) throws LuaException {
        PlayerContext ctx = getPlayerContext(unbaked);
        ServerPlayerEntity player = ctx.player().getPlayer(ctx.server());
        RangedInventoryWrapper wrapped = new RangedInventoryWrapper(player.getInventory(), MAIN_SIZE, 5);
        return wrapInventory(ctx.context(), wrapped);
    }

    public static final SubtargetedModuleMethod<EntityIdentifier.Player> GET_EQUIPMENT = SubtargetedModuleMethod.of(
        "getEquipment", INTROSPECTION_M, EntityIdentifier.Player.class,
        "function():table -- Get this player's held item and armor",
        EntityIntrospectionMethods::getEquipment
    );
    private static FutureMethodResult getEquipment(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                   @Nonnull IArguments args) throws LuaException {
        PlayerContext ctx = getPlayerContext(unbaked);
        ServerPlayerEntity player = ctx.player().getPlayer(ctx.server());
        EquipmentInventoryWrapper wrapped = new EquipmentInventoryWrapper(player);
        return wrapInventory(ctx.context(), wrapped);
    }

    public static final SubtargetedModuleMethod<EntityIdentifier.Player> GET_ENDER_CHEST = SubtargetedModuleMethod.of(
        "getEnder", INTROSPECTION_M, EntityIdentifier.Player.class,
        "function():table -- Get this player's ender chest",
        EntityIntrospectionMethods::getEnder
    );
    private static FutureMethodResult getEnder(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                               @Nonnull IArguments args) throws LuaException {
        PlayerContext ctx = getPlayerContext(unbaked);
        ServerPlayerEntity player = ctx.player().getPlayer(ctx.server());
        return wrapInventory(ctx.context(), player.getEnderChestInventory());
    }

    private static FutureMethodResult wrapInventory(IContext<?> ctx, Inventory inv) {
        // Wrap the Inventory with Plethora's methods (see InventoryWrapper)
        TypedLuaObject<Inventory> object = ctx.makeChildId(inv).getObject();
        return FutureMethodResult.result(object.getMethodNames().length == 0 ? null : object);
    }
}
