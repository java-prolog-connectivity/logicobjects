package org.logicobjects.instrumentation;

import java.util.List;


/**
 * Encapsulates the parsing data of a logic method
 * The different kind of logic methods have different data to parse (e.g., for some of them is important to parse queries, for others no)
 * However, this data always have at most three elements: 
 * - A String representing a logic query
 * - Some Strings representing the method arguments
 * - A String representing the method solution
 * Instances of this class can contain the unparsed data or the data after being parsed.
 * @author scastro
 *
 */
public class LogicMethodParsingData {

	private String queryString;
	private List<String> methodArguments;
	private String solutionString;

	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public List<String> getMethodArguments() {
		return methodArguments;
	}
	public void setMethodArguments(List<String> methodArguments) {
		this.methodArguments = methodArguments;
	}
	public String getSolutionString() {
		return solutionString;
	}
	public void setSolutionString(String solutionString) {
		this.solutionString = solutionString;
	}

	
	
}
