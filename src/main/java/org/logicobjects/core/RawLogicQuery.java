package org.logicobjects.core;

import java.lang.reflect.Method;

import org.jpc.logicengine.LogicEngineConfiguration;
import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;

public abstract class RawLogicQuery extends LogicRoutine {

	protected LQuery aLQuery;
	
	public static boolean isRawQuery(Method method) {
		return !LogicMethod.isLogicMethod(method);//&& (method.isAnnotationPresent(LQuery.class) || method.isAnnotationPresent(LExpression.class));
	}
	
	public static RawLogicQuery create(Method method) {
		LQuery aLQuery = method.getAnnotation(LQuery.class);
		if(aLQuery != null && !aLQuery.value().isEmpty())
			return new MultiPredicateQuery(method);
		else
			return new SimplePredicateQuery(method);
	}
	
	public RawLogicQuery(Method method) {
		super(method);
		aLQuery = (LQuery) getWrappedMethod().getAnnotation(LQuery.class);
	}

	
	@Override
	public String customMethodName() {
		if(aLQuery == null)
			return null;
		return aLQuery.predicate();
	}
	


	

}
