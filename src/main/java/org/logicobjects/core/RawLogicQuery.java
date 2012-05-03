package org.logicobjects.core;

import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.instrumentation.AbstractLogicMethodParser;
import org.logicobjects.instrumentation.RawQueryParser;

public class RawLogicQuery extends AbstractLogicMethod {

	private LQuery aLQuery;
	private String rawQuery;
	
	public static boolean isRawQuery(Method method) {
		return !LogicMethod.isLogicMethod(method) && (method.getAnnotation(LQuery.class) != null || method.getAnnotation(LSolution.class) != null);
	}
	
	public RawLogicQuery(Method method) {
		super(method);
		aLQuery = (LQuery) getWrappedMethod().getAnnotation(LQuery.class);
		if(aLQuery == null) {
			if (getWrappedMethod().getAnnotation(LSolution.class) != null)
				rawQuery = "true";
			else
				throw new RuntimeException("No raw query has been defined for method "+getWrappedMethod().getName());
		}
			
	}

	public String getRawQuery() {
		if(rawQuery == null)
			rawQuery = aLQuery.value();
		return rawQuery;
	}

	@Override
	public Query asQuery(Object targetObject, Object[] javaMethodParams) {
		RawQueryParser parser = (RawQueryParser)AbstractLogicMethodParser.create(getWrappedMethod());
		String queryString = parser.resolveQuery(targetObject, javaMethodParams);
		return new Query(LogicEngine.getDefault().textToTerm(queryString));
	}
	
	
}
