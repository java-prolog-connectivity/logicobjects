package org.logicobjects.adapter.methodresult.solutioncomposition;

import org.jpc.query.Query;

/*
 * Adapt a logic query as an Iterator object (or any interface implemented by SolutionEnumeration, such as Enumeration)
 * It extends from:
 * WrapperAdapter<SolutionEnumeration<LogicAnswerType>, LogicAnswerType>
 * instead of
 * WrapperAdapter<Iterator<LogicAnswerType>,LogicAnswerType>
 * since SolutionEnumeration provides a 'close' method that must be called before disposing the object
 */
public class IteratorWrapperAdapter<LogicAnswerType> extends WrapperAdapter<SolutionEnumeration<LogicAnswerType>, LogicAnswerType> {

	@Override
	public SolutionEnumeration<LogicAnswerType> adapt(Query query) {
		return new SolutionEnumeration(query, getEachSolutionAdapter());
	}

}
