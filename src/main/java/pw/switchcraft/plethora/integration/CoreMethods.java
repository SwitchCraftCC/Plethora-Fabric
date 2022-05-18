package pw.switchcraft.plethora.integration;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.Identifier;
import pw.switchcraft.plethora.api.method.*;
import pw.switchcraft.plethora.api.module.BasicModuleContainer;
import pw.switchcraft.plethora.api.module.IModuleContainer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CoreMethods {
    public static final BasicMethod<IModuleContainer> LIST_MODULES = BasicMethod.of(
        "listModules", "function():table -- Lists all modules available",
        CoreMethods::listModules
    );

    public static final BasicMethod<IModuleContainer> HAS_MODULE = BasicMethod.of(
        "hasModule", "function(module:string):boolean -- Checks whether a module is available",
        CoreMethods::hasModule
    );

    public static final BasicMethod<IModuleContainer> FILTER_MODULES = BasicMethod.of(
        "filterModules", "function(names:string...):table|nil -- Gets the methods which require these modules",
        CoreMethods::filterModules
    );

    public static final BasicMethod<IMethodCollection> GET_DOCS = BasicMethod.of(
        "getDocs", "function([name: string]):string|table -- Get the documentation for all functions or the function specified. Errors if the function cannot be found.",
        CoreMethods::getDocs
    );

    public static FutureMethodResult listModules(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                 @Nonnull IArguments args) throws LuaException {
        IModuleContainer container = unbaked.bake().getTarget();
        Map<Integer, String> modules = new HashMap<>();
        int i = 0;
        for (Identifier module : container.getModules()) {
            modules.put(++i, module.toString());
        }
        return FutureMethodResult.result(modules);
    }

    public static FutureMethodResult hasModule(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                               @Nonnull IArguments args) throws LuaException {
        IModuleContainer container = unbaked.bake().getTarget();
        String module = args.getString(0);
        return FutureMethodResult.result(container.hasModule(new Identifier(module)));
    }

    public static FutureMethodResult filterModules(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                   @Nonnull IArguments args) throws LuaException {
        IContext<IModuleContainer> context = unbaked.bake();
        Set<Identifier> oldModules = context.getTarget().getModules();
        Set<Identifier> newModules = new HashSet<>();

        for (int i = 0; i < args.count(); i++) {
            Identifier module = new Identifier(args.getString(i));
            if (oldModules.contains(module)) newModules.add(module);
        }

        if (newModules.isEmpty()) return null;

        TypedLuaObject<IModuleContainer> object = context
            .<IModuleContainer>makeChildId(new BasicModuleContainer(newModules))
            .getObject();

        return FutureMethodResult.result(object.getMethodNames().length == 0 ? null : object);
    }

    public static FutureMethodResult getDocs(@Nonnull IUnbakedContext<IMethodCollection> unbaked,
                                             @Nonnull IArguments args) throws LuaException {
        IMethodCollection methodCollection = unbaked.bake().getTarget();
        String name = args.optString(0, null);

        if (name == null) {
            Map<String, String> out = new HashMap<>();
            for (IMethod method : methodCollection.methods()) {
                out.put(method.getName(), method.getDocString());
            }

            return FutureMethodResult.result(out);
        } else {
            for (IMethod method : methodCollection.methods()) {
                if (method.getName().equals(name)) return FutureMethodResult.result(method.getDocString());
            }

            throw new LuaException("No such method");
        }
    }
}
