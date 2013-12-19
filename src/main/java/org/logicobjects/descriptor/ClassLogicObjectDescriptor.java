package org.logicobjects.descriptor;

import java.util.Collections;

import org.jpc.util.ParadigmLeakUtil;

public class ClassLogicObjectDescriptor extends LogicObjectDescriptor {
	
	protected final Class<?> guidingClass;
	
	public ClassLogicObjectDescriptor(Class<?> guidingClass) {
		super(
				ParadigmLeakUtil.javaClassNameToProlog(guidingClass.getSimpleName()), //id inferred from the id of the class
				Collections.<String>emptyList(),
				Collections.<String>emptyList(),
				Collections.<String>emptyList(),
				true,
				false
				);
		this.guidingClass = guidingClass;
	}
}
