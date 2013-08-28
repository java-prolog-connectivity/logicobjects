package org.logicobjects.descriptor;

import java.util.Arrays;
import java.util.List;

import org.logicobjects.annotation.LDelegationObject;

public class LDelegationObjectAnnotationDescriptor extends AnnotationLogicObjectDescriptor {
	LDelegationObject aLDelegationObject;

	public LDelegationObjectAnnotationDescriptor(Class guidingClass, LDelegationObject aLDelegationObject) {
		super(guidingClass);
		this.aLDelegationObject = aLDelegationObject;
	}

	@Override
	public String getAnnotatedName() {
		return aLDelegationObject.name();
	}
	
	@Override
	public List<String> args() {
		return Arrays.asList(aLDelegationObject.args());
	}
	
	@Override
	public List<String> imports() {
		return Arrays.asList(aLDelegationObject.imports());
	}

	@Override
	public List<String> modules() {
		return Arrays.asList(aLDelegationObject.modules());
	}

	@Override
	public boolean automaticImport() {
		return aLDelegationObject.automaticImport();
	}


}