package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jpl.Query;

import org.reflectiveutils.GenericsUtil;

/*
 * Adapt a logic query as a List
 */
public class ListWrapperAdapter<LogicAnswerType> extends
		WrapperAdapter<List<LogicAnswerType>, LogicAnswerType> {

	
	@Override
	public List<LogicAnswerType> adapt(Query query) {
		return new SolutionEnumeration(query, getEachSolutionAdapter()).allElements();
	}

	@Override
	public Type getEachSolutionType() {
		//return new GenericsUtil().findAncestorTypeParameters(List.class, getMethodResultType())[0];
		return new GenericsUtil().findDescendantTypeParameters(getMethodResultType(), List.class)[0];
	}
	
}
