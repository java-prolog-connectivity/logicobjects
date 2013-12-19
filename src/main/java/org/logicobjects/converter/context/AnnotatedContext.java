package org.logicobjects.converter.context;

import org.jconverter.Converter;
import org.jpc.Jpc;
import org.jpc.JpcProxy;
import org.logicobjects.descriptor.LogicObjectDescriptor;

//TODO this class needs to be finished. when transforming terms/objects it must use the descriptors and converters if available
public class AnnotatedContext extends JpcProxy {

	private LogicObjectDescriptor logicObjectDescriptor;
	private Converter termToObjectConverter;
	private Converter objectToTermConverter;
	
	public AnnotatedContext(Jpc proxiedJpc, 
			LogicObjectDescriptor logicObjectDescriptor,
			Converter termToObjectConverter,
			Converter objectToTermConverter) {
		super(proxiedJpc);
		this.logicObjectDescriptor = logicObjectDescriptor;
		this.termToObjectConverter = termToObjectConverter;
		this.objectToTermConverter = objectToTermConverter;
	}
	
	public LogicObjectDescriptor getLogicObjectDescriptor() {
		return logicObjectDescriptor;
	}
	
	public Converter getTermToObjectConverter() {
		return termToObjectConverter;
	}
	
	public Converter getObjectToTermConverter() {
		return objectToTermConverter;
	}

	public boolean hasLogicObjectDescriptor() {
		return getLogicObjectDescriptor() != null;
	}
	
	public boolean hasObjectToTermConverter() {
		return getObjectToTermConverter() != null;
	}

	public boolean hasTermToObjectConverter() {
		return getTermToObjectConverter() != null;
	}

}
