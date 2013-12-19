package org.logicobjects.descriptor;

import org.logicobjects.annotation.LConverter;
import org.logicobjects.annotation.LConverter.LConverterUtil;
import org.logicobjects.annotation.LDelegationObjectConverter;
import org.logicobjects.annotation.LDelegationObjectConverter.LDelegationObjectConverterUtil;
import org.minitoolbox.reflection.ReflectionUtil;

public class AnnotationConverterDescriptor extends ConverterDescriptor {

	private final Class<?> annotatedClass;
	private final Object annotation;
	
	public AnnotationConverterDescriptor(Class<?> annotatedClass, LConverter lConverter) {
		super(ReflectionUtil.newInstance(LConverterUtil.getConverterClass(lConverter)), lConverter.preferedClass());
		this.annotatedClass = annotatedClass;
		this.annotation = lConverter;
	}
	
	public AnnotationConverterDescriptor(Class<?> annotatedClass, LDelegationObjectConverter lDelegationObjectConverter) {
		super(ReflectionUtil.newInstance(LDelegationObjectConverterUtil.getConverterClass(lDelegationObjectConverter)), lDelegationObjectConverter.preferedClass());
		this.annotatedClass = annotatedClass;
		this.annotation = lDelegationObjectConverter;
	}

	public Class<?> getAnnotatedClass() {
		return annotatedClass;
	}

	public Object getAnnotation() {
		return annotation;
	}
	
}
