package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Enumeration;

import jpl.Query;

import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

/*
 * Adapt a logic query as an Enumeration object
 * It extends from 
 * WrapperAdapter<SolutionEnumeration<LogicAnswerType>, LogicAnswerType>
 * instead of
 * WrapperAdapter<Enumeration<LogicAnswerType>,LogicAnswerType>
 * since SolutionEnumeration provides a 'close' method that must be called before disposing the object
 */
public class EnumerationWrapperAdapter<LogicAnswerType> extends
		WrapperAdapter<SolutionEnumeration<LogicAnswerType>, LogicAnswerType> {


	@Override
	public SolutionEnumeration<LogicAnswerType> adapt(Query query) {
		return new SolutionEnumeration(query, getEachSolutionAdapter());
	}

	
	@Override
	public Type getEachSolutionType() {
		//return new GenericsUtil().findAncestorTypeParameters(SolutionEnumeration.class, getMethodResultType())[0];
		return new GenericsUtil().findDescendantTypeParameters(getMethodResultType(), SolutionEnumeration.class)[0];
	}
	
}
