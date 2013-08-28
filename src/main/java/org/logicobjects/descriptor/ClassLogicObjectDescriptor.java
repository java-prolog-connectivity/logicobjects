package org.logicobjects.descriptor;

import java.util.Collections;

import org.jpc.util.ParadigmLeakUtil;

public class ClassLogicObjectDescriptor extends LogicObjectDescriptor {
	
	protected Class guidingClass;
	
	public ClassLogicObjectDescriptor(Class guidingClass) {
		super(
				ParadigmLeakUtil.javaClassNameToProlog(guidingClass.getSimpleName()),
				Collections.<String>emptyList(),
				Collections.<String>emptyList(),
				Collections.<String>emptyList(),
				true,
				null
				);
		this.guidingClass = guidingClass;
	}
}
