package org.logicobjects.adapter.adaptingcontext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.core.LogicObjectClass;


public abstract class AnnotatedSingleElementAdaptationContext extends AnnotatedElementAdaptationContext {

	private boolean initializedGudingClass; //a flag to implement caching. The guiding class will be attempted to be discovered only if this variable is false
	
	private Class guidingClass; //a class that guides the mapping from objects to terms (if any)

	public Class getGuidingClass() {
		if(!initializedGudingClass && guidingClass==null) {
			guidingClass = LogicObjectClass.findGuidingClass(getContextClass());
			initializedGudingClass = true;
		}
		return guidingClass;
	}
	
	
	public abstract AnnotatedElement getContext(); //an accessible object can be a method or field
	
	
	protected <A extends Annotation> A getMappingAnnotationLocalContext(Class<A> annotationClass) {
		A annotation = null;
		if(!(getContext() instanceof Class)) { //then it is a field or method
			annotation = getContext().getAnnotation(annotationClass);
		}
		return annotation;
	}
	

	
	
	
}
