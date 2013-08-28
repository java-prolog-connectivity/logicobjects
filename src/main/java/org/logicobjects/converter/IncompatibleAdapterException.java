package org.logicobjects.converter;

import java.lang.reflect.Type;

public class IncompatibleAdapterException extends RuntimeException {
	
	private Class adapterClass;
	private Object object;
	
	public IncompatibleAdapterException(Class adapterClass, Object object) {
		this.adapterClass= adapterClass;
		this.object = object;
	}

	@Override
	public String getMessage() {
		return "The object "+ object+" cannot be adapted by " + adapterClass.getSimpleName();
	}
}
