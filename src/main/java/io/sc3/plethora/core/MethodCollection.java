package io.sc3.plethora.core;

import com.google.common.collect.Lists;
import io.sc3.plethora.api.method.IMethod;
import io.sc3.plethora.api.method.IMethodCollection;

import javax.annotation.Nonnull;
import java.util.List;

public class MethodCollection implements IMethodCollection {
	private final List<IMethod<?>> methods;

	public MethodCollection(List<RegisteredMethod<?>> methods) {
		this.methods = Lists.transform(methods, RegisteredMethod::getMethod);
	}

	@Nonnull
	@Override
	public List<IMethod<?>> methods() {
		return methods;
	}
}
