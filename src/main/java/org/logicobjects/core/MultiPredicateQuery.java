package org.logicobjects.core;

import java.lang.reflect.Method;

import jpl.Query;
import jpl.Term;

import org.logicobjects.annotation.method.LSolution;
import org.logicobjects.instrumentation.AbstractLogicMethodParser;
import org.logicobjects.instrumentation.ParsedLogicMethod;
import org.logicobjects.instrumentation.ParsingData;

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
	public String[] getParameters() {
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
	public ParsingData getDataToParse() {
		ParsingData parsingData = new ParsingData();
		parsingData.setQueryString(getUnparsedQuery());
		parsingData.setParameters(getParameters());
		parsingData.setSolutionString(getEachSolutionValue());
		return parsingData;
	}

	@Override
	protected void computeQueryString(ParsedLogicMethod parsedLogicMethod) {
		parsedLogicMethod.setComputedQueryString(parsedLogicMethod.getParsedData().getQueryString()); 
	}
	
}
