package org.logicobjects.adapter;

import java.lang.reflect.Type;

public class IncompatibleAdapterException extends RuntimeException {
	
	private Type type;
	private Adapter adapter;
	
	public IncompatibleAdapterException(Type type, Adapter adapter) {
		this.type = type;
		this.adapter= adapter;
	}
	
	@Override
	public String getMessage() {
		return "The type "+type.toString()+" is not compatible with the adapter "+adapter.toString();
	}
}
