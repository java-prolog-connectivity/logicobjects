package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.util.List;

import jpl.Query;

import org.logicobjects.core.SolutionEnumeration;

/*
 * Adapt a logic query as a List
 */
public class ListCompositionAdapter<LogicAnswerType> extends
		SolutionCompositionAdapter<List<LogicAnswerType>, LogicAnswerType> {

	@Override
	public List<LogicAnswerType> adapt(Query query) {
		return new SolutionEnumeration(query, getEachSolutionAdapter()).allElements();
	}
/*
	@Override
	public Type getEachSolutionType() {
		AbstractTypeWrapper[] wrapperdParametersTypes = new GenericsUtil().findParametersInstantiations(Iterable.class, getMethodResultType());
		return wrapperdParametersTypes[0].getWrappedType();
	}
	*/
}
