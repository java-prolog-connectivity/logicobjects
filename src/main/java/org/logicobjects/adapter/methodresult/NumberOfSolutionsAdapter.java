package org.logicobjects.adapter.methodresult;

import java.lang.reflect.Method;

import jpl.Query;

public class NumberOfSolutionsAdapter extends MethodResultAdapter<Integer> {

	public NumberOfSolutionsAdapter(Method method, Object targetObject, Object[] javaMethodParams) {
		super(method, targetObject, javaMethodParams);
	}
	
	@Override
	public Integer adapt(Query query) {
		return query.allSolutions().length;
	}

}
