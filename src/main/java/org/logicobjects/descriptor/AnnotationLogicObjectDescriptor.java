package org.logicobjects.descriptor;

import static java.util.Arrays.asList;

import org.jpc.util.ParadigmLeakUtil;
import org.logicobjects.annotation.LDelegationObject;
import org.logicobjects.annotation.LObject;


public class AnnotationLogicObjectDescriptor extends LogicObjectDescriptor {

	private final Class annotatedClass;
	private final Object annotation;
	
	public AnnotationLogicObjectDescriptor(Class annotatedClass, LObject lObject) {
		super(
				lObject.name().isEmpty()?ParadigmLeakUtil.javaClassNameToProlog(annotatedClass.getSimpleName()):lObject.name(),
				asList(lObject.args()),
				asList(lObject.imports()),
				asList(lObject.modules()),
				lObject.automaticImport(),
				lObject.referenceTerm(),
				lObject.termIndex()
				);
		this.annotatedClass = annotatedClass;
		this.annotation = lObject;
	}
	
	public AnnotationLogicObjectDescriptor(Class annotatedClass, LDelegationObject lDelegationObject) {
		super(
				lDelegationObject.name().isEmpty()?ParadigmLeakUtil.javaClassNameToProlog(annotatedClass.getSimpleName()):lDelegationObject.name(),
				asList(lDelegationObject.args()),
				asList(lDelegationObject.imports()),
				asList(lDelegationObject.modules()),
				lDelegationObject.automaticImport(),
				lDelegationObject.referenceTerm(),
				lDelegationObject.termIndex()
				);
		this.annotatedClass = annotatedClass;
		this.annotation = lDelegationObject;
	}

}
