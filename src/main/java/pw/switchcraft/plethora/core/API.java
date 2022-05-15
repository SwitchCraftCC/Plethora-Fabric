package pw.switchcraft.plethora.core;

import pw.switchcraft.plethora.api.PlethoraAPI;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleRegistry;

public final class API implements PlethoraAPI.IPlethoraAPI {
	@Override
	public IMethodRegistry methodRegistry() {
		return MethodRegistry.instance;
	}

	@Override
	public IMetaRegistry metaRegistry() {
		return null; // TODO: Meta registry
		// return MetaRegistry.instance;
	}

	@Override
	public IModuleRegistry moduleRegistry() {
		return ModuleRegistry.instance;
	}
}
