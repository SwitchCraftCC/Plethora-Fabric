package io.sc3.plethora.core;

import io.sc3.plethora.api.PlethoraAPI;
import io.sc3.plethora.api.converter.IConverterRegistry;
import io.sc3.plethora.api.meta.IMetaRegistry;
import io.sc3.plethora.api.method.IMethodRegistry;
import io.sc3.plethora.api.module.IModuleRegistry;

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
