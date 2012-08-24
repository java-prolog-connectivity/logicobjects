package org.logicobjects.instrumentation;

import jpl.Query;
import jpl.Term;

import org.logicobjects.core.AbstractLogicMethod;

/**
 * This class represents a fully parsed logic method
 * A logic method can be interpreted with its execution context: the object it belongs to and its runtime parameters 
 * @author scastro
 *
 */
public class ParsedLogicMethod {

	private AbstractLogicMethod logicMethod; //the logic method to be parsed
	private Object targetObject; //the instance of the class providing context for the parsing
	private Object[] originalParameters; //the parameters sent to the method providing context to the parsing (after being adapted by the individual adapters and parameters array adapter if any)
	private String computedQueryString; //the query
	private String computedMethodName; //this is here just because in the future it can be interesting to parse the method name (now it is just a constant)
	private Object[] computedParameters; //the parameters
	private ParsingData parsedData;
	
	public ParsedLogicMethod(AbstractLogicMethod logicMethod, Object targetObject, Object[] originalParameters, ParsingData parsedData) {
		this.logicMethod = logicMethod;
		this.targetObject = targetObject;
		this.originalParameters = originalParameters;
		this.parsedData = parsedData;
	}
	
	public AbstractLogicMethod getLogicMethod() {
		return logicMethod;
	}
	
	public Object getTargetObject() {
		return targetObject;
	}

	public Object[] getOriginalParameters() {
		return originalParameters;
	}
	
	public ParsingData getParsedData() {
		return parsedData;
	}

	public String getComputedQueryString() {
		return computedQueryString;
	}

	public void setComputedQueryString(String computedQueryString) {
		this.computedQueryString = computedQueryString;
	}

	public Object[] getComputedParameters() {
		return computedParameters;
	}

	public void setComputedParameters(Object[] computedParameters) {
		this.computedParameters = computedParameters;
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
