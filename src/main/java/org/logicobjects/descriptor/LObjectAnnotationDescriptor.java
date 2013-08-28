package org.logicobjects.descriptor;

import java.util.Arrays;
import java.util.List;

import org.logicobjects.annotation.LObject;

public class LObjectAnnotationDescriptor extends AnnotationLogicObjectDescriptor {
	LObject aLObject;

	public static LogicObjectDescriptor create(Class guidingClass, LObject aLObject) {
		return new LObjectAnnotationDescriptor(guidingClass, aLObject);
	}
	
	public static LogicObjectDescriptor create(Class guidingClass, LDelegationObject aLDelegationObject) {
		return new LDelegationObjectAnnotationDescriptor(guidingClass, aLDelegationObject);
	}
	
	public LObjectAnnotationDescriptor(Class guidingClass, LObject aLObject) {
		super(guidingClass);
		this.aLObject = aLObject;
	}

	@Override
	public String getAnnotatedName() {
		return aLObject.name();
	}
	
	@Override
	public List<String> args() {
		return Arrays.asList(aLObject.args());
	}

	@Override
	public List<String> imports() {
		return Arrays.asList(aLObject.imports());
	}

	@Override
	public List<String> modules() {
		return Arrays.asList(aLObject.modules());
	}

	@Override
	public boolean automaticImport() {
		return aLObject.automaticImport();
	}

}