package org.logicobjects.adapter.methodresult;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import jpl.Query;

import org.logicobjects.adapter.LogicAdapter;
import org.logicobjects.adapter.methodresult.eachsolution.EachSolutionAdapter;

/*
 * Abstract base class for all the adapters that adapt a logic query as an object of type MethodResultType
 */
public abstract class MethodResultAdapter<MethodResultType> extends LogicAdapter<Query, MethodResultType> {
	
	public static class DefaultMethodResultAdapter extends MethodResultAdapter<Query> {
		@Override
		public Query adapt(Query source) {
			return source;
		}
		
		public DefaultMethodResultAdapter(Method method) {
			super(method);
		}
	}
	
	public MethodResultAdapter() {
	}
	
	public MethodResultAdapter(Method method) {
		setMethod(method);
	}
	
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
