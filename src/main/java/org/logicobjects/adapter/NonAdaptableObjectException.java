package org.logicobjects.adapter;


/**
 * Thrown when an adapter receives an object which a type or properties that cannot be adapted
 * @author scastro
 *
 */
public class NonAdaptableObjectException extends RuntimeException {

	private Class adapterClass;
	private Object object;
	
	public NonAdaptableObjectException(Class adapterClass,
			Object object) {
		this.adapterClass = adapterClass;
		this.object = object;
	}
	
	@Override
	public String getMessage() {
		return "The object " + object + " cannot be adapted by adapter " + adapterClass.getSimpleName();
	}
	
}
