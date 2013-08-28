package org.logicobjects.descriptor;

import static java.util.Arrays.asList;

import org.jpc.util.ParadigmLeakUtil;
import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LObject;


public abstract class AnnotationLogicObjectDescriptor extends LogicObjectDescriptor {

	private Class guidingClass;
	private Object annotation;
	
	public AnnotationLogicObjectDescriptor(Class guidingClass, LDelegationObject lDelegationObject) {
		super(
				lDelegationObject.name().isEmpty()?ParadigmLeakUtil.javaClassNameToProlog(guidingClass.getSimpleName()):lDelegationObject.name(),
				asList(lDelegationObject.args()),
				asList(lDelegationObject.imports()),
				asList(lDelegationObject.modules()),
				lDelegationObject.automaticImport(),
				lDelegationObject.id().isEmpty()?null:lDelegationObject.id()
				);
		this.guidingClass = guidingClass;
		this.annotation = lDelegationObject;
	}
	
	public AnnotationLogicObjectDescriptor(Class guidingClass, LObject lObject) {
		super(
				lObject.name().isEmpty()?ParadigmLeakUtil.javaClassNameToProlog(guidingClass.getSimpleName()):lObject.name(),
				asList(lObject.args()),
				asList(lObject.imports()),
				asList(lObject.modules()),
				lObject.automaticImport(),
				lObject.id().isEmpty()?null:lObject.id()
				);
		this.guidingClass = guidingClass;
		this.annotation = lObject;
	}

}
