package pw.switchcraft.plethora.api;

import pw.switchcraft.plethora.api.converter.IConverterRegistry;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleRegistry;

import java.lang.reflect.InvocationTargetException;

/**
 * API entry point for Plethora
 */
public final class PlethoraAPI {
    private PlethoraAPI() {
    }

    public interface IPlethoraAPI {
        IConverterRegistry converterRegistry();

        IMetaRegistry metaRegistry();

        IMethodRegistry methodRegistry();

        IModuleRegistry moduleRegistry();
    }

    private static final IPlethoraAPI API;

    /**
     * Get the main API entry point
     *
     * @return Main API entry point
     */
    public static IPlethoraAPI instance() {
        return API;
    }

    static {
        // TODO: This is probably the wrong way to do this for a Fabric API
        IPlethoraAPI api;
        final String name = "pw.switchcraft.plethora.core.API";
        try {
            Class<?> registryClass = Class.forName(name);
            api = (IPlethoraAPI) registryClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new CoreNotFoundException("Cannot load Plethora API as " + name + " cannot be found", e);
        } catch (InstantiationException e) {
            throw new CoreNotFoundException("Cannot load Plethora API as " + name + " cannot be created", e);
        } catch (IllegalAccessException e) {
            throw new CoreNotFoundException("Cannot load Plethora API as " + name + " cannot be accessed", e);
        } catch (InvocationTargetException e) {
            throw new CoreNotFoundException("Cannot load Plethora API as " + name + " cannot be constructed", e);
        } catch (NoSuchMethodException e) {
            throw new CoreNotFoundException("Cannot load Plethora API as " + name + "'s constructor cannot be found", e);
        }
        API = api;
    }
}
