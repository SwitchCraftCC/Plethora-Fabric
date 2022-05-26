package pw.switchcraft.plethora.integration.vanilla.registry;

import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.IMethod;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.integration.vanilla.method.EntityIntrospectionMethods;
import pw.switchcraft.plethora.integration.vanilla.method.EntityKineticMethods;

public final class VanillaMethodRegistration {
    public static void registerMethods(IMethodRegistry r) {
        moduleMethod(r, "introspection:getInventory", EntityIntrospectionMethods.GET_INVENTORY);
        moduleMethod(r, "introspection:getEquipment", EntityIntrospectionMethods.GET_EQUIPMENT);
        moduleMethod(r, "introspection:getEnder", EntityIntrospectionMethods.GET_ENDER_CHEST);

        moduleMethod(r, "kinetic:look", EntityKineticMethods.LOOK);
        moduleMethod(r, "kinetic:use", EntityKineticMethods.USE);
        moduleMethod(r, "kinetic:swing", EntityKineticMethods.SWING);
    }

    private static <T> void method(IMethodRegistry r, String name, Class<T> target, IMethod<T> method) {
        r.registerMethod(Identifier.DEFAULT_NAMESPACE, name, target, method);
    }

    private static <T> void method(IMethodRegistry r, Class<T> target, IMethod<T> method) {
        r.registerMethod(Identifier.DEFAULT_NAMESPACE, method.getName(), target, method);
    }

    private static void moduleMethod(IMethodRegistry r, String name, IMethod<IModuleContainer> method) {
        method(r, name, IModuleContainer.class, method);
    }
}