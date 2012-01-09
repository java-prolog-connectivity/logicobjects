package org.logicobjects.adapter;

/*
 * Parent of all logic adapters (adapters that convert to/from terms) in the system
 * 
 */
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

	public void setParameters(Object ...parameters) {
		this.parameters = parameters;
	}


	
}
