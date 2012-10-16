package org.logicobjects.adapter.adaptingcontext;

import java.lang.reflect.AnnotatedElement;

public class ClassAdaptationContext extends AnnotatedSingleElementAdaptationContext {

	private Class clazz;
	
	public ClassAdaptationContext(Class clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public AnnotatedElement getContext() {
		return clazz;
	}
	
	@Override
	public Class getContextClass() {
		return clazz;
	}
	





	
}

