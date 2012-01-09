package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Type;
import java.util.List;

import jpl.Query;

import org.reflectiveutils.AbstractTypeWrapper;
import org.reflectiveutils.GenericsUtil;

/*
 * Adapt a logic query as a List
 */
public class ListCompositionAdapter<LogicAnswerType> extends
		WrapperAdapter<List<LogicAnswerType>, LogicAnswerType> {

	@Override
	public List<LogicAnswerType> adapt(Query query) {
		return new SolutionEnumeration(query, getEachSolutionAdapter()).allElements();
	}

	@Override
	public Type getEachSolutionType() {
		AbstractTypeWrapper[] wrapperdParametersTypes = new GenericsUtil().findParametersInstantiations(List.class, getMethodResultType());
		return wrapperdParametersTypes[0].getWrappedType();
	}
	
}
