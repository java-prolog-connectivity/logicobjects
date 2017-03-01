package org.logicobjects.core;

import java.lang.reflect.Method;
import java.util.List;

import org.jpc.term.Term;
import org.logicobjects.instrumentation.LogicMethodParsingData;
import org.logicobjects.instrumentation.ParsedLogicMethod;

public class MultiPredicateQuery extends RawLogicQuery {

	public MultiPredicateQuery(Method method) {
		super(method);
		if(aLQuery == null || aLQuery.value().isEmpty()) {
			throw new RuntimeException("No query has been defined for method "+getWrappedMethod().getName());
		}
		
		/*
		if(aLQuery != null) {
			setQueryString(aLQuery.value());
			setUnparsedQueryString(true);
		}
		if(getQueryString() == null || getQueryString().isEmpty())
			throw new RuntimeException("No query has been defined for method "+getWrappedMethod().getNameTerm());
			*/
	}

	@Override
	public List<String> getLogicMethodArguments() {
		return null;
	}

	@Override
	public Term asGoal(ParsedLogicMethod parsedLogicMethod) {
		String queryString = parsedLogicMethod.getComputedQueryString();
		return logicUtil.asTerm(queryString);
	}
	
	public String getUnparsedQuery() {
		return aLQuery.value();
	}
	
	@Override
	public LogicMethodParsingData getDataToParse() {
		LogicMethodParsingData parsingData = new LogicMethodParsingData();
		parsingData.setQueryString(getUnparsedQuery());
		parsingData.setMethodArguments(getLogicMethodArguments());
		parsingData.setSolutionString(getEachSolutionValue());
		return parsingData;
	}

	@Override
	protected void configureParsedLogicMethodQueryString(ParsedLogicMethod parsedLogicMethod) {
		parsedLogicMethod.setComputedQueryString(parsedLogicMethod.getParsedData().getQueryString()); 
	}
	
}
