package org.logicobjects.converter.context;

import org.jpc.Jpc;
import org.jpc.JpcProxy;
import org.jpc.converter.JpcConverter;
import org.logicobjects.descriptor.LogicObjectDescriptor;

//TODO this class needs to be finished. when transforming terms/objects it must use the descriptors and converters if available
public class AnnotatedContext extends JpcProxy {

	private LogicObjectDescriptor logicObjectDescriptor;
	private JpcConverter termToObjectConverter;
	private JpcConverter objectToTermConverter;
	
	public AnnotatedContext(Jpc proxiedJpc, 
			LogicObjectDescriptor logicObjectDescriptor,
			JpcConverter termToObjectConverter,
			JpcConverter objectToTermConverter) {
		super(proxiedJpc);
		this.logicObjectDescriptor = logicObjectDescriptor;
		this.termToObjectConverter = termToObjectConverter;
		this.objectToTermConverter = objectToTermConverter;
	}
	
	public LogicObjectDescriptor getLogicObjectDescriptor() {
		return logicObjectDescriptor;
	}
	
	public JpcConverter getTermToObjectConverter() {
		return termToObjectConverter;
	}
	
	public JpcConverter getObjectToTermConverter() {
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
