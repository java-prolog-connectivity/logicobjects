package org.logicobjects.adapter.methodresult.solutioncomposition;

import jpl.Query;

import org.logicobjects.core.SolutionEnumeration;

/*
 * Adapt a logic query as an Enumeration object
 */
public class EnumerationCompositionAdapter<LogicAnswerType> extends
		SolutionCompositionAdapter<SolutionEnumeration<LogicAnswerType>, LogicAnswerType> {


	@Override
	public SolutionEnumeration<LogicAnswerType> adapt(Query query) {
		return new SolutionEnumeration(query, getEachSolutionAdapter());
	}

	/*
	@Override
	public Type getEachSolutionType() {
		AbstractTypeWrapper[] wrapperdParametersTypes = new GenericsUtil().findParametersInstantiations(Enumeration.class, getMethodResultType());
		return wrapperdParametersTypes[0].getWrappedType();
	}
	*/
}
