package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.Method;

import org.logicobjects.util.LogicUtil;

public class MethodAdaptingContext extends AccessibleObjectAdaptingContext {
	private Method method;
	
	public MethodAdaptingContext(Method method) {
		this.method = method;
	}
	
	public Method getContext() {
		return method;
	}

	@Override
	public String infereLogicObjectName() {
		return LogicUtil.javaClassNameToProlog(method.getDeclaringClass().getSimpleName());
	}
	
}
