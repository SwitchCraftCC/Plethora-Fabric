package pw.switchcraft.plethora.integration.computercraft.registry;

import dan200.computercraft.api.ComputerCraftAPI;
import pw.switchcraft.plethora.api.method.IMethod;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.integration.computercraft.method.TurtleKineticMethods;

public final class ComputerCraftMethodRegistration {
    public static void registerMethods(IMethodRegistry r) {
        // Modules
        moduleMethod(r, "kinetic:use", TurtleKineticMethods.USE);
        moduleMethod(r, "kinetic:swing", TurtleKineticMethods.SWING);
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
}
