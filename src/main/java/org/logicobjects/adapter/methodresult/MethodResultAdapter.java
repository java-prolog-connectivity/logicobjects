package org.logicobjects.adapter.methodresult;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import jpl.Query;

import org.logicobjects.adapter.LogicAdapter;

/*
 * Abstract base class for all the adapters that adapt a logic query as an object of type MethodResultType
 */
public abstract class MethodResultAdapter<MethodResultType> extends LogicAdapter<Query, MethodResultType> {
	
	private Method method;
	/*
	public MethodResultAdapter(Object ...parameters) {
		super(parameters);
	}
	*/

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	public Type getMethodResultType() {
		return method.getGenericReturnType();
	}
	
	
}
