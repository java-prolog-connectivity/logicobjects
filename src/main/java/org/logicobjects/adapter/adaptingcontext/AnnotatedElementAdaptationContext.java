package org.logicobjects.adapter.adaptingcontext;

import java.lang.annotation.Annotation;

import org.logicobjects.adapter.ObjectToTermAdapter;
import org.logicobjects.adapter.TermToObjectAdapter;
import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectAdapter;
import org.logicobjects.annotation.LObjectAdapter.LObjectAdapterUtil;
import org.logicobjects.annotation.LTermAdapter;
import org.logicobjects.annotation.LTermAdapter.LTermAdapterUtil;
import org.logicobjects.core.LogicObjectClass;

public abstract class AnnotatedElementAdaptationContext extends JavaAdaptationContext {

	public abstract Class getContextClass();
	
	public LObjectAdapter getTermToObjectAdapterAnnotation() {
		return getMappingAnnotation(LObjectAdapter.class);
	}

	public LTermAdapter getObjectToTermAdapterAnnotation() {
		return getMappingAnnotation(LTermAdapter.class);
	}
	
	public AbstractLogicObjectDescriptor getLogicObjectDescription() {
		LObject aLObject = getMappingAnnotation(LObject.class);
		if(aLObject != null)
			return AbstractLogicObjectDescriptor.create(aLObject);
		else
			return null;
	}
	
	protected <A extends Annotation> A getMappingAnnotationGuidingClass(Class<A> annotationClass) {
		A annotation = null;
		Class guidingClass = getGuidingClass();
		if(guidingClass != null) {
			annotation = (A) guidingClass.getAnnotation(annotationClass);
		}
		return annotation;
	}
	
	protected <A extends Annotation> A getMappingAnnotation(Class<A> annotationClass) {
		A annotation = getMappingAnnotationLocalContext(annotationClass);
		if(annotation == null && !shouldIgnoreHierarchyMappingAnnotations()) {
			annotation = (A) getMappingAnnotationGuidingClass(annotationClass);
		}
		return annotation;
	}
	
	protected abstract <A extends Annotation> A getMappingAnnotationLocalContext(Class<A> annotationClass);

	/**
	 * 
	 * @return a boolean indicating if there are mapping annotations that should be found in the hierarchy
	 * by default, if the guiding class (the first class in the hierarchy identified as a logic class) implements ITermObject then any annotation present in classes in the hierarchy should be ignored 
	 */
	public boolean shouldIgnoreHierarchyMappingAnnotations() {
		return getGuidingClass() != null && LogicObjectClass.isTermObjectClass(getGuidingClass());
	}

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


}
