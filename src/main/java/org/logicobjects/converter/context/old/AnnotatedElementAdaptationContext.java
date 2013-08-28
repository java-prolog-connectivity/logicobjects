package org.logicobjects.converter.context.old;

import java.lang.annotation.Annotation;

import org.logicobjects.annotation.LObject;
import org.logicobjects.annotation.LObjectConverter;
import org.logicobjects.annotation.LObjectConverter.LObjectConverterUtil;
import org.logicobjects.annotation.LTermConverter;
import org.logicobjects.annotation.LTermConverter.LTermConverterUtil;
import org.logicobjects.converter.old.ObjectToTermConverter;
import org.logicobjects.converter.old.TermToObjectConverter;
import org.logicobjects.core.LogicClass;
import org.logicobjects.descriptor.LogicObjectDescriptor;

public abstract class AnnotatedElementAdaptationContext extends JavaAdaptationContext {

	public abstract Class getContextClass();
	
	public LObjectConverter getTermToObjectConverterAnnotation() {
		return getMappingAnnotation(LObjectConverter.class);
	}

	public LTermConverter getObjectToTermConverterAnnotation() {
		return getMappingAnnotation(LTermConverter.class);
	}
	
	public LogicObjectDescriptor getLogicObjectDescription() {
		LObject aLObject = getMappingAnnotation(LObject.class);
		if(aLObject != null)
			return LogicObjectDescriptor.create(aLObject);
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
		return getGuidingClass() != null && LogicClass.isTermObjectClass(getGuidingClass());
	}

	public TermToObjectConverter getTermToObjectConverter() {
		LObjectConverter aLObjectAdapter = getTermToObjectConverterAnnotation();
		if(aLObjectAdapter != null)
			return LObjectConverterUtil.newConverter(aLObjectAdapter);
		return null;
	}



	public ObjectToTermConverter getObjectToTermConverter() {
		LTermConverter aLTermAdapter = getObjectToTermConverterAnnotation();
		if(aLTermAdapter != null)
			return LTermConverterUtil.newConverter(aLTermAdapter);
		return null;
	}


}
