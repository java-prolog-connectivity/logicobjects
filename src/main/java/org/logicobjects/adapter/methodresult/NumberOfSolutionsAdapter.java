package org.logicobjects.adapter.methodresult;

import java.lang.reflect.Method;

import jpl.Query;

public class NumberOfSolutionsAdapter extends MethodResultAdapter<Integer> {

	public NumberOfSolutionsAdapter(Method method) {
		super(method);
	}
	
	@Override
	public Integer adapt(Query query) {
		return query.allSolutions().length;
	}

}
