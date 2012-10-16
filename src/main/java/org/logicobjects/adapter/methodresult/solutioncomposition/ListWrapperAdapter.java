package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.util.List;

import org.logicobjects.term.Query;

/*
 * Adapt a logic query as a List
 */
public class ListWrapperAdapter<LogicAnswerType> extends WrapperAdapter<List<LogicAnswerType>, LogicAnswerType> {

	@Override
	public List<LogicAnswerType> adapt(Query query) {
		return new SolutionEnumeration(query, getEachSolutionAdapter()).allElements();
	}

}
