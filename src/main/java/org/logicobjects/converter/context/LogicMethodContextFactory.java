package org.logicobjects.converter.context;

import java.lang.reflect.Method;

import org.jpc.Jpc;
import org.jpc.converter.JpcConverter;
import org.logicobjects.annotation.LObjectConverter;
import org.logicobjects.annotation.LObjectConverter.LObjectConverterUtil;

public class LogicMethodContextFactory extends AnnotatedContextFactory {

	public LogicMethodContextFactory(Jpc jpcContext) {
		super(jpcContext);
	}

	public AnnotatedContext create(Method method) {
		JpcConverter termToObjectConverter = null;
		LObjectConverter lObjectConverter = method.getAnnotation(LObjectConverter.class);
		if(lObjectConverter != null) {
			termToObjectConverter = LObjectConverterUtil.newConverter(lObjectConverter);
		}
		return new AnnotatedContext(jpcContext, null, termToObjectConverter, null);
	}
	
}
