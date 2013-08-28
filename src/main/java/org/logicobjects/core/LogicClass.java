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

}
