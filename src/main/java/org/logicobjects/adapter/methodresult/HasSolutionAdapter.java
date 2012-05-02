package org.logicobjects.adapter.methodresult;

import java.lang.reflect.Method;

import jpl.Query;


/*
 * Adapt a logic query has a boolean indicating if the query has or not a solution
 */
public class HasSolutionAdapter extends MethodResultAdapter<Boolean> {

	public HasSolutionAdapter(Method method, Object targetObject, Object[] javaMethodParams) {
		super(method, targetObject, javaMethodParams);
	}
	
	@Override
	public Boolean adapt(Query source) {
		return source.hasSolution();
	}

}
