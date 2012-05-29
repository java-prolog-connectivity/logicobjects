package org.logicobjects.adapter;

public class BadExpressionException extends RuntimeException {
	private String methodName;
	private String expression;
	
	public BadExpressionException(String methodName, String expression) {
		this.methodName = methodName;
		this.expression = expression;
	}
	
	@Override
	public String getMessage() {
		return "The expression: " + expression + " in the generated method: " + methodName + " is not valid.";
	}
	
}
