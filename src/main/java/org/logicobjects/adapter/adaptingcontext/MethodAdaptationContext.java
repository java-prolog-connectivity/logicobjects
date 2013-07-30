package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Method;

import org.jpc.util.PrologUtil;

public class MethodAdaptationContext extends AnnotatedSingleElementAdaptationContext {
	private Method method;
	
	public MethodAdaptationContext(Method method) {
		this.method = method;
	}
	
	public Method getContext() {
		return method;
	}

	@Override
	public Class getContextClass() {
		return method.getReturnType();
	}
	
}
