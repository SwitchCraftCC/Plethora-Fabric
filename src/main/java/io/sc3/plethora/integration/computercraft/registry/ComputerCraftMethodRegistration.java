package io.sc3.plethora.integration.computercraft.registry;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.inventory.Inventory;
import io.sc3.plethora.api.method.IMethod;
import io.sc3.plethora.api.method.IMethodRegistry;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.integration.computercraft.InventoryMethodsWrapper;
import io.sc3.plethora.integration.computercraft.method.TurtleKineticMethods;

public final class ComputerCraftMethodRegistration {
    public static void registerMethods(IMethodRegistry r) {
        // Modules
        moduleMethod(r, "kinetic:use", TurtleKineticMethods.USE);
        moduleMethod(r, "kinetic:swing", TurtleKineticMethods.SWING);

        // Inventory wrapper
        inventoryMethod(r, "getSize", InventoryMethodsWrapper.GET_SIZE);
        inventoryMethod(r, "list", InventoryMethodsWrapper.LIST);
        inventoryMethod(r, "getItemDetail", InventoryMethodsWrapper.GET_ITEM_DETAIL);
        inventoryMethod(r, "getItemLimit", InventoryMethodsWrapper.GET_ITEM_LIMIT);
        inventoryMethod(r, "pushItems", InventoryMethodsWrapper.PUSH_ITEMS);
        inventoryMethod(r, "pullItems", InventoryMethodsWrapper.PULL_ITEMS);
    }

    private static <T> void method(IMethodRegistry r, String name, Class<T> target, IMethod<T> method) {
        r.registerMethod(ComputerCraftAPI.MOD_ID, name, target, method);
    }

    private static <T> void method(IMethodRegistry r, Class<T> target, IMethod<T> method) {
        r.registerMethod(ComputerCraftAPI.MOD_ID, method.getName(), target, method);
    }

    private static void moduleMethod(IMethodRegistry r, String name, IMethod<IModuleContainer> method) {
        method(r, name, IModuleContainer.class, method);
    }

    private static void inventoryMethod(IMethodRegistry r, String name, IMethod<Inventory> method) {
        method(r, "inventory:" + name, Inventory.class, method);
    }
}
