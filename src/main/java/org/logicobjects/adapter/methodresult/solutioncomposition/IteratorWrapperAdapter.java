package org.logicobjects.adapter.methodresult.solutioncomposition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;

import jpl.Query;

import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.reflectiveutils.GenericsUtil;
import org.reflectiveutils.wrappertype.AbstractTypeWrapper;

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
