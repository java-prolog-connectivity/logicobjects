package org.logicobjects.adapter;

import org.logicobjects.adapter.adaptingcontext.AdaptationContext;

/**
 * Thrown when an adapter receives an adaptation context that cannot recognize
 * @author scastro
 *
 */
public class UnrecognizedAdaptationContextException extends RuntimeException {

	private Class adapterClass;
	private AdaptationContext context;
	

	public UnrecognizedAdaptationContextException(Class adapterClass,
			AdaptationContext context) {
		this.adapterClass = adapterClass;
		this.context = context;
	}
	
	@Override
	public String getMessage() {
		return "The adapter class " + adapterClass.getSimpleName() + " cannot recognize the context " + context;
	}
}
