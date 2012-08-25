package org.logicobjects.instrumentation;

import jpl.Query;
import jpl.Term;

import org.logicobjects.core.AbstractLogicMethod;

/**
 * This class represents a fully parsed logic method
 * A logic method can be interpreted with its execution context: the object it belongs to and its runtime method arguments 
 * @author scastro
 *
 */
public class ParsedLogicMethod {

	private AbstractLogicMethod logicMethod; //the logic method to be parsed
	private Object targetObject; //the instance of the class providing context for the parsing
	private Object[] originalMethodArguments; //the original method arguments sent to the method providing context to the parsing (after being adapted by the individual adapters and method arguments array adapter if any)
	
	private LogicMethodParsingData parsedData; //the parsed string data
	
	private String computedQueryString; //the query
	private String computedMethodName; //this is here just because in the future it can be interesting to parse the method name (now it is just a constant)
	private Object[] computedMethodArguments; //the method arguments
	
	/**
	 * 
	 * @param logicMethod knows how to configure this object given a parsedData object
	 * @param targetObject the object that receives the logic method invocation
	 * @param originalMethodArguments the arguments of the logic method
	 * @param parsedData contains the parsed method data
	 */
	public ParsedLogicMethod(AbstractLogicMethod logicMethod, Object targetObject, Object[] originalMethodArguments, LogicMethodParsingData parsedData) {
		this.logicMethod = logicMethod;
		this.targetObject = targetObject;
		this.originalMethodArguments = originalMethodArguments;
		this.parsedData = parsedData;
		logicMethod.configureParsedLogicMethod(this);
	}
	
	public AbstractLogicMethod getLogicMethod() {
		return logicMethod;
	}
	
	public Object getTargetObject() {
		return targetObject;
	}

	public Object[] getOriginalMethodArguments() {
		return originalMethodArguments;
	}
	
	public LogicMethodParsingData getParsedData() {
		return parsedData;
	}

	/*
	public String getComputedMethodName() {
		
	}
	
	public String getComputedQueryString() {
		return parsedData.getQueryString();
	}
	
	public Object[] getComputedMethodArguments() {
		return parsedData.getMethodArguments();
	}
	
	public String getComputedSolutionString() {
		return parsedData.getSolutionString();
	}
	*/
	
	public String getComputedQueryString() {
		return computedQueryString;
	}

	public void setComputedQueryString(String computedQueryString) {
		this.computedQueryString = computedQueryString;
	}

	public Object[] getComputedMethodArguments() {
		return computedMethodArguments;
	}

	public void setComputedMethodArguments(Object[] computedMethodArguments) {
		this.computedMethodArguments = computedMethodArguments;
	}

	public String getComputedMethodName() {
		return computedMethodName;
	}

	public void setComputedMethodName(String computedMethodName) {
		this.computedMethodName = computedMethodName;
	}

	
	public Query asQuery() {
		return logicMethod.asQuery(this);
	}
	
	public Term getEachSolutionTerm() {
		return logicMethod.getEachSolutionTerm(this);
	}
	
	
}
