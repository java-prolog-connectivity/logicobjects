package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Type;

import jpl.Query;

import org.logicobjects.core.SolutionEnumeration;

public class OneAnswerAdapter<LogicAnswerType> extends SolutionCompositionAdapter<LogicAnswerType, LogicAnswerType> {

	@Override
	public LogicAnswerType adapt(Query query) {
		LogicAnswerType answer = null;
		SolutionEnumeration<LogicAnswerType> solutionEnumeration = new SolutionEnumeration<LogicAnswerType>(query, getEachSolutionAdapter());
		try {
			answer = solutionEnumeration.nextElement();
		} finally {
			solutionEnumeration.close();
		}
		return answer;
	}

	@Override
	public Type getEachSolutionType() {
		return getMethodResultType();
	}
}
