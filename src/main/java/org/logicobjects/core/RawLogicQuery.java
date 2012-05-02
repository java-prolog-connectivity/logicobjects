package org.logicobjects.core;

import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.annotation.method.LQuery;
import org.logicobjects.instrumentation.AbstractLogicMethodParser;
import org.logicobjects.instrumentation.RawQueryParser;

public class RawLogicQuery extends AbstractLogicMethod {

	private LQuery aLQuery;
	
	public RawLogicQuery(Method method) {
		super(method);
		aLQuery = (LQuery) getWrappedMethod().getAnnotation(LQuery.class);
		if(aLQuery == null)
			throw new RuntimeException("No raw query has been defined for method "+getWrappedMethod().getName());
	}

	public String getRawQuery() {
		return aLQuery.value();
	}

	@Override
	public Query asQuery(Object targetObject, Object[] javaMethodParams) {
		RawQueryParser parser = (RawQueryParser)AbstractLogicMethodParser.create(getWrappedMethod());
		String queryString = parser.resolveQuery(targetObject, javaMethodParams);
		return new Query(LogicEngine.getDefault().textToTerm(queryString));
	}
	
	
}
