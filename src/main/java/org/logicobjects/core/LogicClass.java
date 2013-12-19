package org.logicobjects.core;

import org.logicobjects.descriptor.ConverterDescriptor;
import org.logicobjects.descriptor.LogicObjectDescriptor;
import org.minitoolbox.reflection.typewrapper.SingleTypeWrapper;

/**
 * A class providing a description (i.e., mapping information) for instantiating logic objects
 * Part of the data may be in the logic side
 * @author scastro
 *
 */
public class LogicClass extends SingleTypeWrapper {

	//mapping descriptors
	private LogicObjectDescriptor defaultTermDescriptor;
	private LogicObjectDescriptor methodInvokerDescriptor;
	//converter descriptors
	private ConverterDescriptor defaultTermConverterDescriptor;
	private ConverterDescriptor methodInvokerConverterDescriptor;
	
	public LogicClass(Class clazz) {
		super(clazz);
	}
	
	public LogicClass(Class clazz, LogicObjectDescriptor termDescriptor, LogicObjectDescriptor methodInvokerDescriptor, 
			ConverterDescriptor termConverterDescriptor, ConverterDescriptor methodInvokerConverterDescriptor) {
		super(clazz);
		this.defaultTermDescriptor = termDescriptor;
		this.methodInvokerDescriptor = methodInvokerDescriptor;
		this.defaultTermConverterDescriptor = termConverterDescriptor;
		this.methodInvokerConverterDescriptor = methodInvokerConverterDescriptor;
	}

	public LogicObjectDescriptor getDefaultTermDescriptor() {
		return defaultTermDescriptor;
	}

	public LogicObjectDescriptor getMethodInvokerDescriptor() {
		return methodInvokerDescriptor;
	}

	public ConverterDescriptor getDefaultTermConverterDescriptor() {
		return defaultTermConverterDescriptor;
	}

	public ConverterDescriptor getMethodInvokerConverterDescriptor() {
		return methodInvokerConverterDescriptor;
	}

	
	
//	public boolean hasNoArgsConstructor(Class clazz) {
//		Constructor[] constructors = clazz.getConstructors();
//		if(constructors.length == 0) //implicit constructor
//			return true;
//		try {
//			clazz.getConstructor(); //if this method does not thrown a NoSuchMethodException exception, then there is a non-parameters constructor
//			return true;
//		} catch (NoSuchMethodException e) {
//			return false;
//		} catch (SecurityException e) {
//			throw new RuntimeException(e);
//		}
//	}
//	
//	public boolean hasConstructorWithArgsNumber(Class clazz, int n) {
//		for(Constructor constructor : clazz.getConstructors()) {
//			if(constructor.getParameterTypes().length == n)
//				return true;
//		}	
//		return false;
//	}
	
	/**
	 * Answers if a class has a constructor with only one declared argument that happens no be a variable args constructor
	 * @param clazz
	 * @return
	 */
//	public boolean hasConstructorWithOneVarArgs(Class clazz) {
//		for(Constructor constructor : clazz.getConstructors()) {
//			if(constructor.getParameterTypes().length == 1 && constructor.isVarArgs())
//				return true;
//		}	
//		return false;
//	}
	
}
