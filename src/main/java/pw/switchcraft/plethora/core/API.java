package pw.switchcraft.plethora.core;

import pw.switchcraft.plethora.api.PlethoraAPI;
import pw.switchcraft.plethora.api.converter.IConverterRegistry;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.api.method.IMethodRegistry;
import pw.switchcraft.plethora.api.module.IModuleRegistry;

public final class API implements PlethoraAPI.IPlethoraAPI {
	@Override
	public IConverterRegistry converterRegistry() {
		return ConverterRegistry.instance;
	}

	@Override
	public IMetaRegistry metaRegistry() {
		return MetaRegistry.instance;
	}

	@Override
	public IMethodRegistry methodRegistry() {
		return MethodRegistry.instance;
	}

	@Override
	public IModuleRegistry moduleRegistry() {
		return ModuleRegistry.instance;
	}
}
