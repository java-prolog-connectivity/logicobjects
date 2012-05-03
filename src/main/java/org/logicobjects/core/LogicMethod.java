package org.logicobjects.core;

import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.adapter.LogicObjectAdapter;
import org.logicobjects.adapter.adaptingcontext.MethodInvokerContext;
import org.logicobjects.adapter.methodparameters.TermParametersAdapter;
import org.logicobjects.annotation.method.LMethod;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.util.AnnotationConstants;
import org.logicobjects.util.LogicUtil;

public class LogicMethod extends AbstractLogicMethod {

	private LMethod aLMethod;
	
	public static boolean isLogicMethod(Method method) {
		return method.getAnnotation(LMethod.class) != null;
	}
	
	public LogicMethod(Method method) {
		super(method);
		aLMethod = (LMethod) getWrappedMethod().getAnnotation(LMethod.class);
		if(aLMethod == null)
			throw new RuntimeException("The method "+getWrappedMethod().getName() + " is not a logic method");
	}
	
	public String getLogicName() {
		String name = aLMethod.name(); //method indicated at the annotation
		if(!name.isEmpty())
			return name;
		else
			return LogicUtil.javaNameToProlog(getWrappedMethod().getName()); //if no name is provided in the annotation, answer the method name after converting it to prolog naming conventions
	}
	
	/**
	 * 
	 * @return the method parameters as specified in the annotation
	 */
	public String[] getParameters() {
		if(AnnotationConstants.isNullArray(aLMethod.params()))
			return null;
		return aLMethod.params();
	}

	@Override
	public Query asQuery(Object targetObject, Object[] params) {
		String logicMethodName = getLogicName();
		LogicObject lo = new LogicObjectAdapter().adapt(targetObject, new MethodInvokerContext(targetObject.getClass()));
		if(declaresLogicParameters()) {
			TermParametersAdapter paramsAdapter = new TermParametersAdapter(getWrappedMethod(), targetObject);
			params = paramsAdapter.adapt(params);
		}
		return lo.invokeMethod(logicMethodName, params);
	}
	
	public boolean declaresLogicParameters() {
		return getParameters() != null;
	}

}
