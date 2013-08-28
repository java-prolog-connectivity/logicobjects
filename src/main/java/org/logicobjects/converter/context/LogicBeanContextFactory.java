package org.logicobjects.converter.context;

import org.jpc.Jpc;
import org.jpc.converter.JpcConverter;
import org.logicobjects.annotation.LObjectConverter;
import org.logicobjects.annotation.LObjectConverter.LObjectConverterUtil;
import org.logicobjects.annotation.LTermConverter;
import org.logicobjects.descriptor.LogicObjectDescriptor;


public class LogicBeanContextFactory extends AnnotatedContextFactory {

	public LogicBeanContextFactory(Jpc jpcContext) {
		super(jpcContext);
	}

	public AnnotatedContext create(Class clazz, String propertyName) {
		LogicBeanProperty logicBeanProperty = new LogicBeanProperty(clazz, propertyName);
		LogicObjectDescriptor logicObjectDescriptor = LogicObjectDescriptor.create(logicBeanProperty.getLObject());
		JpcConverter termToObjectConverter = null;
		JpcConverter objectToTermConverter = null;
		LObjectConverter lObjectConverter = logicBeanProperty.getLObjectConverter();
		if(lObjectConverter != null)
			termToObjectConverter = LObjectConverterUtil.newConverter(lObjectConverter);
		LTermConverter lTermConverter = logicBeanProperty.getLTermConverter();
		if(lTermConverter != null)
			objectToTermConverter = LTermConverterUtil.newConverter(lTermConverter);
		return new AnnotatedContext(jpcContext, logicObjectDescriptor, termToObjectConverter, objectToTermConverter);
	}
	
}
