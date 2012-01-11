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
	
	
	
	/*
	 * This method is a hack. 
	 * Methods generated with Javassist ignore Generics information, that in this context is useful for introspection.
	 * Given that here the generated methods are overridden abstract methods, when asked for the method type this method will try to answer the type of the "same" method defined in a super class
	 * where the method type has the Generics information.
	 * This could be changed if used another byte code instrumentation library that does not ignore generics (maybe ASM, but not tried it yet)
	 */
	public Type getMethodResultType() {
		try {
			Class superClass = method.getDeclaringClass().getSuperclass();
			Method superMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
			return superMethod.getGenericReturnType();
		} catch (NoSuchMethodException e) {
			return method.getGenericReturnType();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
//		return method.getGenericReturnType();
	}
	

}
