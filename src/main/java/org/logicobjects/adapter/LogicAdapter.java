package org.logicobjects.adapter;


public abstract class LogicAdapter<From, To> extends Adapter<From, To> {
	//public static final String VARIABLE_PREFIX = "?";  //to review this ...
	
	private Object[] parameters;
	
	public LogicAdapter() {
		setParameters(new Object[] {});
	}
	
	/*
	public ParametrizedAdapter(Object ...parameters) {
		setParameters(parameters);
	}
	*/
	
	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}


	
}
