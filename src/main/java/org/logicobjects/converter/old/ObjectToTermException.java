package org.logicobjects.converter.old;

public class ObjectToTermException extends RuntimeException {
	private Object source;
	public ObjectToTermException(Object source) {
		this.source = source;
	}
	
	@Override
	public String getMessage() {
		return "Impossible to transform object " + source.toString() + " of class " + source.getClass().getName() + " as a term object";
	}
}
