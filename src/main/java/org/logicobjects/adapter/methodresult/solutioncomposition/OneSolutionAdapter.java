package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import jpl.Query;


public class OneSolutionAdapter<LogicSolutionType> extends SolutionCompositionAdapter<LogicSolutionType, LogicSolutionType> {

	public OneSolutionAdapter(Method method, Object targetObject, Object[] javaMethodParams) {
		super(method, targetObject, javaMethodParams);
	}
	
	@Override
	public LogicSolutionType adapt(Query query) {
		LogicSolutionType solution = null;
		SolutionEnumeration<LogicSolutionType> solutionEnumeration = new SolutionEnumeration<LogicSolutionType>(query, getEachSolutionAdapter());
		try {
			solution = solutionEnumeration.nextElement();
		} finally {
			solutionEnumeration.close();
		}
		return solution;
	}

	@Override
	public Type getEachSolutionType() {
		return getMethodResultType();
	}

}
