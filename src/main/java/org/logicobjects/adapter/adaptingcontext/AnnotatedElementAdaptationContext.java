package org.logicobjects.adapter.adaptingcontext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LObjectAdapter.LObjectAdapterUtil;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.annotation.LTermAdapter.LTermAdapterUtil;
import org.logicobjects.core.LogicClass;

public abstract class AnnotatedElementAdaptationContext extends AdaptationContext {

	private Class guidingClass; //a class that guides the mapping from objects to terms (if any)
	private boolean initializedGudingClass; //a flag to implement caching. The guiding class will be attempted to be discovered only if this variable is false
	
	
	//protected abstract TermToObjectAdapter getTermToObjectAdapter();

	//public abstract ObjectToTermAdapter getObjectToTermAdapter();

	//protected abstract LogicObjectDescriptor getLogicObjectDescription();
	
	public abstract Class getContextClass();

	public Class getGuidingClass() {
		if(!initializedGudingClass && guidingClass==null) {
			guidingClass = LogicClass.findGuidingClass(getContextClass());
			initializedGudingClass = true;
		}
		return guidingClass;
	}
	
	public boolean shouldIgnoreHierarchyMappingAnnotations() {
		return getGuidingClass() != null && LogicClass.isTermObjectClass(getGuidingClass());
	}
	
	public boolean hasObjectToTermAdapter() {
		return getObjectToTermAdapter() != null;
	}

	public boolean hasTermToObjectAdapter() {
		return getTermToObjectAdapter() != null;
	}

	public boolean hasLogicObjectDescription() {
		return getLogicObjectDescription() != null;
	}
	
	
	
	

	
	
	
	//
	public abstract AnnotatedElement getContext(); //an accessible object can be a method or field
	
	public TermToObjectAdapter getTermToObjectAdapter() {
		LObjectAdapter aLObjectAdapter = getTermToObjectAdapterAnnotation();
		if(aLObjectAdapter != null)
			return LObjectAdapterUtil.newAdapter(aLObjectAdapter);
		return null;
	}

	public ObjectToTermAdapter getObjectToTermAdapter() {
		LTermAdapter aLTermAdapter = getObjectToTermAdapterAnnotation();
		if(aLTermAdapter != null)
			return LTermAdapterUtil.newAdapter(aLTermAdapter);
		return null;
	}
	
	
	public LObjectAdapter getTermToObjectAdapterAnnotation() {
		return getMappingAnnotation(LObjectAdapter.class);
	}


	public LTermAdapter getObjectToTermAdapterAnnotation() {
		return getMappingAnnotation(LTermAdapter.class);
	}
	
	public LogicObjectDescriptor getLogicObjectDescription() {
		LObject aLObject = getMappingAnnotation(LObject.class);
		if(aLObject != null)
			return LogicObjectDescriptor.create(aLObject);
		else
			return null;
	}
	
	private <A extends Annotation> A getMappingAnnotation(Class<A> annotationClass) {
		A annotation = null;
		if(!(getContext() instanceof Class)) { //then it is a field or method
			annotation = getContext().getAnnotation(annotationClass);
		}
		if(annotation == null && !shouldIgnoreHierarchyMappingAnnotations()) {
			Class guidingClass = getGuidingClass();
			if(guidingClass != null)
				annotation = (A) guidingClass.getAnnotation(annotationClass);
		}
		return annotation;
	}

}
