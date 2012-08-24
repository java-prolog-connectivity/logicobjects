package org.logicobjects.instrumentation;



public class ParsingData {

	private String queryString;
	private String[] parameters;
	private String solutionString;

	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String[] getParameters() {
		return parameters;
	}
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	public String getSolutionString() {
		return solutionString;
	}
	public void setSolutionString(String solutionString) {
		this.solutionString = solutionString;
	}

	
	
}
