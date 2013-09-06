package org.logicobjects.descriptor;

import org.logicobjects.annotation.LConverter;
import org.logicobjects.annotation.LConverter.LConverterUtil;
import org.logicobjects.annotation.LDelegationObjectConverter;
import org.logicobjects.annotation.LDelegationObjectConverter.LDelegationObjectConverterUtil;

public class AnnotationConverterDescriptor extends ConverterDescriptor {

	private Class guidingClass;
	private Object annotation;
	
	public AnnotationConverterDescriptor(Class guidingClass, LConverter lConverter) {
		super(LConverterUtil.getConverterClass(lConverter), lConverter.preferedClass());
		this.guidingClass = guidingClass;
		this.annotation = lConverter;
	}
	
	public AnnotationConverterDescriptor(Class guidingClass, LDelegationObjectConverter lDelegationObjectConverter) {
		super(LDelegationObjectConverterUtil.getConverterClass(lDelegationObjectConverter), lDelegationObjectConverter.preferedClass());
		this.guidingClass = guidingClass;
		this.annotation = lDelegationObjectConverter;
	}

}
