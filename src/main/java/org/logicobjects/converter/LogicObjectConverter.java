package org.logicobjects.converter;

import org.jpc.mapping.converter.JpcConverter;
import org.logicobjects.converter.descriptor.LogicObjectDescriptor;


public class LogicObjectConverter implements JpcConverter {

	protected final LogicObjectDescriptor descriptor;

	public LogicObjectConverter(LogicObjectDescriptor descriptor) {
		this.descriptor = descriptor;
	}
	
}
