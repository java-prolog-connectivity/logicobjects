package org.logicobjects.instrumentation;

import java.util.List;

import org.jpc.term.AbstractTerm;
import org.jpc.term.Term;
import org.logicobjects.core.LogicRoutine;

/**
 * This class represents a fully parsed logic method
 * A logic method can be interpreted with its execution context: the object it belongs to and its runtime method arguments 
 * @author scastro
 *
 */
public class ParsedLogicMethod {

	private LogicRoutine logicMethod; //the logic method to be parsed
	private Object targetObject; //the instance of the class providing context for the parsing
	private List originalMethodArguments; //the original method arguments sent to the method providing context to the parsing (after being adapted by the individual adapters and method arguments array adapter if any)
	
	private LogicMethodParsingData parsedData; //the parsed string data
	
	private String computedQueryString; //the query
	private String computedMethodName;
	private List computedMethodArguments; //the method arguments
	
	/**
	 * 
	 * @param logicMethod knows how to configure this object given a parsedData object
	 * @param targetObject the object that receives the logic method invocation
	 * @param originalMethodArguments the arguments of the logic method
	 * @param parsedData contains the parsed method data
	 */
	public ParsedLogicMethod(LogicRoutine logicMethod, Object targetObject, List originalMethodArguments, LogicMethodParsingData parsedData) {
		this.logicMethod = logicMethod;
		this.targetObject = targetObject;
		this.originalMethodArguments = originalMethodArguments;
		this.parsedData = parsedData;
		logicMethod.configureParsedLogicMethod(this);
	}
	
	public LogicRoutine getLogicMethod() {
		return logicMethod;
	}
	
	public Object getTargetObject() {
		return targetObject;
	}

	public List getOriginalMethodArguments() {
		return originalMethodArguments;
	}
	
	public LogicMethodParsingData getParsedData() {
		return parsedData;
	}
	
	public String getComputedQueryString() {
		return computedQueryString;
	}

	public void setComputedQueryString(String computedQueryString) {
		this.computedQueryString = computedQueryString;
	}

	public List getComputedMethodArguments() {
		return computedMethodArguments;
	}

	public void setComputedMethodArguments(List computedMethodArguments) {
		this.computedMethodArguments = computedMethodArguments;
	}

	public String getComputedMethodName() {
		return computedMethodName;
	}

	public void setComputedMethodName(String computedMethodName) {
		this.computedMethodName = computedMethodName;
	}

	
	public Term asGoal() {
		return logicMethod.asGoal(this);
	}
	
	public AbstractTerm getEachSolutionTerm() {
		return logicMethod.getEachSolutionTerm(this);
	}
	
	
}
