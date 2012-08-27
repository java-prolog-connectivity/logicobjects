package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Method;

import org.logicobjects.util.LogicUtil;

public class MethodAdaptationContext extends AnnotatedElementAdaptationContext {
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
