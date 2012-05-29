package org.logicobjects.adapter.methodresult;

import java.lang.reflect.Method;

import org.logicobjects.instrumentation.ParsedLogicMethod;

import jpl.Query;

public class NumberOfSolutionsAdapter extends MethodResultAdapter<Integer> {

	
	@Override
	public Integer adapt(Query query) {
		return query.allSolutions().length;
	}

}
