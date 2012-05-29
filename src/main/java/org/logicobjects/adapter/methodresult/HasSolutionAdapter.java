package org.logicobjects.adapter.methodresult;

import java.lang.reflect.Method;

import org.logicobjects.instrumentation.ParsedLogicMethod;

import jpl.Query;


/*
 * Adapt a logic query has a boolean indicating if the query has or not a solution
 */
public class HasSolutionAdapter extends MethodResultAdapter<Boolean> {

	
	@Override
	public Boolean adapt(Query source) {
		return source.hasSolution();
	}

}
