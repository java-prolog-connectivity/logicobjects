package org.logicobjects.core;

import java.lang.reflect.Method;

import jpl.Query;

import org.logicobjects.annotation.method.LExpression;
import org.logicobjects.annotation.method.LQuery.LQueryUtil;
import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.instrumentation.LogicMethodParsingData;
import org.logicobjects.instrumentation.ParsedLogicMethod;

public class SimplePredicateQuery extends RawLogicQuery {

	public SimplePredicateQuery(Method method) {
		super(method);
		if(aLQuery == null && !isAnnotationPresent(LExpression.class))
			throw new RuntimeException("No query has been defined for method "+getWrappedMethod().getName());
	}

	/**
	 * 
	 * @return a boolean representing if the method answers a logic expression or the result of a query
	 */
	public boolean isLogicExpression() {
		return aLQuery == null && (/*isAnnotationPresent(LSolution.class) ||*/ isAnnotationPresent(LExpression.class));
	}
	
	/**
	 * 
	 * @return the query arguments as specified in the annotation
	 */
	public String[] getLogicMethodArguments() {
		if(aLQuery != null)
			return LQueryUtil.getArgs(aLQuery);
		return null;
	}
	
	@Override
	public Query asQuery(ParsedLogicMethod parsedLogicMethod) {
		if(isLogicExpression())
			return new Query(parsedLogicMethod.getComputedQueryString());
		else
			return new Query(asTerm(parsedLogicMethod));
	}

	@Override
	public LogicMethodParsingData getDataToParse() {
		LogicMethodParsingData parsingData = new LogicMethodParsingData();
		//TODO this code is duplicated, taking from LogicMethod
		if(hasCustomMethodName()) //this is to avoid parsing method names such as $1. This is a valid Java method name, but would be interpreted by the parser as "a String given by the first argument of the logic method"
			parsingData.setQueryString(logicMethodName());
		parsingData.setMethodArguments(getLogicMethodArguments());
		parsingData.setSolutionString(getEachSolutionValue());
		return parsingData;
	}

	@Override
	protected void configureParsedLogicMethodQueryString(ParsedLogicMethod parsedLogicMethod) {
		if(isLogicExpression()) {
			parsedLogicMethod.setComputedQueryString("true");
		} else {
			super.configureParsedLogicMethodQueryString(parsedLogicMethod); //the query will be the method name
		}
	}
	
}
