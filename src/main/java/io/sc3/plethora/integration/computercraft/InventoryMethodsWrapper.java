package io.sc3.plethora.integration.computercraft;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.shared.peripheral.generic.methods.InventoryMethods;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.inventory.Inventory;
import io.sc3.plethora.api.method.BasicMethod;
import io.sc3.plethora.api.method.FutureMethodResult;
import io.sc3.plethora.api.method.IContext;
import io.sc3.plethora.api.method.IUnbakedContext;

import javax.annotation.Nonnull;
import java.util.Optional;

import static io.sc3.plethora.core.ContextHelpers.fromContext;

public class InventoryMethodsWrapper {
    public static final BasicMethod<Inventory> GET_SIZE = BasicMethod.of(
        "size", "function():number -- Get the size of this inventory.",
        InventoryMethodsWrapper::size
    );
    public static FutureMethodResult size(IUnbakedContext<Inventory> unbaked, IArguments args) throws LuaException {
        Inventory inv = getInventory(unbaked);
        return FutureMethodResult.result(inv.size());
    }

    public static final BasicMethod<Inventory> LIST = BasicMethod.of(
        "list", "function():table -- List all items in this inventory. This returns a table, with an entry for each slot.",
        InventoryMethodsWrapper::list
    );
    public static FutureMethodResult list(IUnbakedContext<Inventory> unbaked, IArguments args) throws LuaException {
        Inventory inv = getInventory(unbaked);
        return FutureMethodResult.result(InventoryMethods.list(InventoryStorage.of(inv, null)));
    }

    public static final BasicMethod<Inventory> GET_ITEM_DETAIL = BasicMethod.of(
        "getItemDetail", "function(slot:number):table -- Get detailed information about an item.",
        InventoryMethodsWrapper::getItemDetail
    );
    public static FutureMethodResult getItemDetail(IUnbakedContext<Inventory> unbaked, IArguments args) throws LuaException {
        int slot = args.getInt(0);
        Inventory inv = getInventory(unbaked);
        return FutureMethodResult.result(InventoryMethods.getItemDetail(InventoryStorage.of(inv, null), slot));
    }

    public static final BasicMethod<Inventory> GET_ITEM_LIMIT = BasicMethod.of(
        "getItemLimit", "function(slot:number):number -- Get the maximum number of items which can be stored in this slot.",
        InventoryMethodsWrapper::getItemLimit
    );
    public static FutureMethodResult getItemLimit(IUnbakedContext<Inventory> unbaked, IArguments args) throws LuaException {
        int slot = args.getInt(0);
        Inventory inv = getInventory(unbaked);
        return FutureMethodResult.result(InventoryMethods.getItemLimit(InventoryStorage.of(inv, null), slot));
    }

    public static final BasicMethod<Inventory> PUSH_ITEMS = BasicMethod.of(
        "pushItems", "function(toName:string, fromSlot:number, [limit:number], [toSlot:number]):number " +
            "-- Push items from one inventory to another connected one.",
        InventoryMethodsWrapper::pushItems
    );
    public static FutureMethodResult pushItems(IUnbakedContext<Inventory> unbaked, IArguments args) throws LuaException {
        String toName = args.getString(0);
        int fromSlot = args.getInt(1);
        Optional<Integer> limit = args.optInt(2);
        Optional<Integer> toSlot = args.optInt(3);

        Context ctx = getContext(unbaked);

        return FutureMethodResult.result(InventoryMethods.pushItems(InventoryStorage.of(ctx.inventory, null), ctx.access, toName, fromSlot, limit, toSlot));
    }

    public static final BasicMethod<Inventory> PULL_ITEMS = BasicMethod.of(
        "pullItems", "function(fromName:string, fromSlot:number, [limit:number], [toSlot:number]):number " +
            "-- Pull items from a connected inventory into this one.",
        InventoryMethodsWrapper::pullItems
    );
    public static FutureMethodResult pullItems(IUnbakedContext<Inventory> unbaked, IArguments args) throws LuaException {
        String fromName = args.getString(0);
        int fromSlot = args.getInt(1);
        Optional<Integer> limit = args.optInt(2);
        Optional<Integer> toSlot = args.optInt(3);

        Context ctx = getContext(unbaked);

        return FutureMethodResult.result(InventoryMethods.pullItems(InventoryStorage.of(ctx.inventory, null), ctx.access, fromName, fromSlot, limit, toSlot));
    }

    private static Inventory getInventory(@Nonnull IUnbakedContext<Inventory> unbaked) throws LuaException {
        return unbaked.bake().getTarget();
    }

    private record Context(IContext<Inventory> context, Inventory inventory, IComputerAccess access) {}
    private static Context getContext(@Nonnull IUnbakedContext<Inventory> unbaked) throws LuaException {
        IContext<Inventory> ctx = unbaked.bake();
        IComputerAccess access = fromContext(ctx, IComputerAccess.class);
        return new Context(ctx, ctx.getTarget(), access);
    }
}
