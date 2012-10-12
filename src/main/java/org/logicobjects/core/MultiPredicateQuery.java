package org.logicobjects.core;

import java.lang.reflect.Method;

import jpl.Query;
import jpl.Term;

import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.instrumentation.LogicMethodParser;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.logicobjects.instrumentation.LogicMethodParsingData;

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
			throw new RuntimeException("No query has been defined for method "+getWrappedMethod().getName());
			*/
	}

	@Override
	public String[] getLogicMethodArguments() {
		return null;
	}

	@Override
	public Query asQuery(ParsedLogicMethod parsedLogicMethod) {
		String queryString = parsedLogicMethod.getComputedQueryString();
		Term term = LogicEngine.getDefault().textToTerm(queryString);
		return new Query(term);
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
